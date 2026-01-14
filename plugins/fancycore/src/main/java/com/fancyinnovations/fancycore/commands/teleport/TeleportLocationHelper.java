package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.HashMap;
import java.util.Map;

public class TeleportLocationHelper {

    /**
     * Saves the current location of a player as their previous location (for /back command).
     * This should be called before teleporting the player.
     *
     * @param fp The FancyPlayer to save the location for
     * @param ref The entity reference
     * @param store The entity store
     * @param world The world the player is in
     */
    public static void savePreviousLocation(FancyPlayer fp, Ref<EntityStore> ref, Store<EntityStore> store, World world) {
        TransformComponent transformComponent = (TransformComponent) store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) {
            return;
        }

        HeadRotation headRotationComponent = (HeadRotation) store.getComponent(ref, HeadRotation.getComponentType());
        if (headRotationComponent == null) {
            return;
        }

        Map<String, Object> backLocation = new HashMap<>();
        backLocation.put("world", world.getName());
        backLocation.put("x", transformComponent.getPosition().getX());
        backLocation.put("y", transformComponent.getPosition().getY());
        backLocation.put("z", transformComponent.getPosition().getZ());
        backLocation.put("yaw", headRotationComponent.getRotation().getYaw());
        backLocation.put("pitch", headRotationComponent.getRotation().getPitch());
        fp.getData().setCustomData("teleport_back_location", backLocation);
    }
}
