package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TeleportBackCMD extends CommandBase {

    public TeleportBackCMD() {
        super("back", "Teleports you back to your previous location before your last teleport");
        // TODO: Permission check
        // requirePermission("fancycore.commands.back");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        PlayerRef senderPlayerRef = fp.getPlayer();
        if (senderPlayerRef == null) {
            ctx.sendMessage(Message.raw("You are not online."));
            return;
        }

        Ref<EntityStore> senderRef = senderPlayerRef.getReference();
        if (senderRef == null || !senderRef.isValid()) {
            ctx.sendMessage(Message.raw("You are not in a world."));
            return;
        }

        // Get previous location from customData
        Map<String, Object> customData = fp.getData().getCustomData();
        Object backLocationObj = customData.get("teleport_back_location");
        if (backLocationObj == null || !(backLocationObj instanceof Map)) {
            ctx.sendMessage(Message.raw("You do not have a previous location to teleport back to."));
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> backLocation = (Map<String, Object>) backLocationObj;
        String worldName = (String) backLocation.get("world");
        Double x = ((Number) backLocation.get("x")).doubleValue();
        Double y = ((Number) backLocation.get("y")).doubleValue();
        Double z = ((Number) backLocation.get("z")).doubleValue();
        Double yaw = ((Number) backLocation.get("yaw")).doubleValue();
        Double pitch = ((Number) backLocation.get("pitch")).doubleValue();

        Store<EntityStore> senderStore = senderRef.getStore();
        World currentWorld = ((EntityStore) senderStore.getExternalData()).getWorld();

        // Get target world
        World targetWorld = com.hypixel.hytale.server.core.universe.Universe.get().getWorld(worldName);
        if (targetWorld == null) {
            ctx.sendMessage(Message.raw("The world you were in is no longer loaded."));
            return;
        }

        // Get current position to save as new back location
        currentWorld.execute(() -> {
            TransformComponent transformComponent = (TransformComponent) senderStore.getComponent(senderRef, TransformComponent.getComponentType());
            if (transformComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your transform."));
                return;
            }

            HeadRotation headRotationComponent = (HeadRotation) senderStore.getComponent(senderRef, HeadRotation.getComponentType());
            if (headRotationComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your head rotation."));
                return;
            }

            // Save current location as new back location
            Map<String, Object> newBackLocation = new java.util.HashMap<>();
            newBackLocation.put("world", currentWorld.getName());
            newBackLocation.put("x", transformComponent.getPosition().getX());
            newBackLocation.put("y", transformComponent.getPosition().getY());
            newBackLocation.put("z", transformComponent.getPosition().getZ());
            newBackLocation.put("yaw", headRotationComponent.getRotation().getYaw());
            newBackLocation.put("pitch", headRotationComponent.getRotation().getPitch());
            fp.getData().setCustomData("teleport_back_location", newBackLocation);

            // Execute teleportation on the target world thread
            targetWorld.execute(() -> {
                // Create transform from previous location
                Transform destinationTransform = new Transform(
                        new com.hypixel.hytale.math.vector.Vector3d(x, y, z),
                        new com.hypixel.hytale.math.vector.Vector3f((float) yaw.doubleValue(), (float) pitch.doubleValue(), 0.0f)
                );

                // Create teleport component
                Teleport teleport = new Teleport(targetWorld, destinationTransform);

                // Add teleport component to sender
                senderStore.addComponent(senderRef, Teleport.getComponentType(), teleport);

                // Send success message
                ctx.sendMessage(Message.raw("Teleported back to your previous location."));
            });
        });
    }
}
