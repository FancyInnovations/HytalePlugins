package com.fancyinnovations.fancycore.utils;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.util.concurrent.atomic.AtomicReference;

public class ComponentProvider {

    public static Player toPlayer(FancyPlayer fp) {
        // TODO Improve this method to not use busy waiting
        // maybe cache the player component in FancyPlayer

        if (fp.getPlayer() == null || fp.getPlayer().getWorldUuid() == null) {
            return null;
        }

        World world = Universe.get().getWorld(fp.getPlayer().getWorldUuid());
        if (world == null) {
            return null;
        }

        Ref<EntityStore> ref = fp.getPlayer().getReference();
        AtomicReference<Player> player = new AtomicReference<>();
        world.execute(() -> {
            player.set(ref.getStore().getComponent(ref, Player.getComponentType()));
        });

        // Wait for the player component to be available
        // Busy waiting for a maximum of 1 second
        for (int i = 0; i < 100; i++) {
            if (player.get() != null) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                FancyCorePlugin.get().getFancyLogger().warn("Interrupted while waiting for player component", ThrowableProperty.of(e));
            }
        }


        return player.get();
    }

    public static TransformComponent toTransform(FancyPlayer fp) {
        // TODO Improve this method to not use busy waiting
        // maybe cache the player component in FancyPlayer

        if (fp.getPlayer() == null || fp.getPlayer().getWorldUuid() == null) {
            return null;
        }

        World world = Universe.get().getWorld(fp.getPlayer().getWorldUuid());
        if (world == null) {
            return null;
        }

        Ref<EntityStore> ref = fp.getPlayer().getReference();
        AtomicReference<TransformComponent> transform = new AtomicReference<>();
        world.execute(() -> {
            transform.set(ref.getStore().getComponent(ref, TransformComponent.getComponentType()));
        });

        // Wait for the transform component to be available
        // Busy waiting for a maximum of 1 second
        for (int i = 0; i < 100; i++) {
            if (transform.get() != null) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                FancyCorePlugin.get().getFancyLogger().warn("Interrupted while waiting for transform component", ThrowableProperty.of(e));
            }
        }


        return transform.get();
    }

}
