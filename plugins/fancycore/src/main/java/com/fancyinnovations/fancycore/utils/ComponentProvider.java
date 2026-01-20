package com.fancyinnovations.fancycore.utils;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class for retrieving entity components from a FancyPlayer.
 */
public class ComponentProvider {

    private static final long TIMEOUT_MS = 1000;

    /**
     * Get the Player component for a FancyPlayer.
     *
     * @param fp the FancyPlayer
     * @return the Player component, or null if not available
     */
    public static Player toPlayer(FancyPlayer fp) {
        return getComponent(fp, Player.getComponentType(), Player.class);
    }

    /**
     * Get the TransformComponent for a FancyPlayer.
     *
     * @param fp the FancyPlayer
     * @return the TransformComponent, or null if not available
     */
    public static TransformComponent toTransform(FancyPlayer fp) {
        return getComponent(fp, TransformComponent.getComponentType(), TransformComponent.class);
    }

    /**
     * Generic method to retrieve a component from a FancyPlayer.
     * Uses CountDownLatch instead of busy waiting for better performance.
     *
     * @param fp            the FancyPlayer
     * @param componentType the component type to retrieve
     * @param clazz         the class of the component (for type safety in logging)
     * @param <T>           the component type
     * @return the component, or null if not available within the timeout
     */
    private static <T extends Component<EntityStore>> T getComponent(
            FancyPlayer fp,
            ComponentType<EntityStore, T> componentType,
            Class<T> clazz
    ) {
        PlayerRef playerRef = fp.getPlayer();
        if (playerRef == null || playerRef.getWorldUuid() == null) {
            return null;
        }

        World world = Universe.get().getWorld(playerRef.getWorldUuid());
        if (world == null) {
            return null;
        }

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return null;
        }

        AtomicReference<T> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        world.execute(() -> {
            try {
                Store<EntityStore> store = ref.getStore();
                if (store != null) {
                    T component = store.getComponent(ref, componentType);
                    result.set(component);
                }
            } finally {
                latch.countDown();
            }
        });

        try {
            if (!latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                FancyCorePlugin.get().getFancyLogger().warn(
                        "Timeout waiting for " + clazz.getSimpleName() + " component for player " + fp.getData().getUsername()
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            FancyCorePlugin.get().getFancyLogger().warn(
                    "Interrupted while waiting for " + clazz.getSimpleName() + " component",
                    ThrowableProperty.of(e)
            );
        }

        return result.get();
    }
}
