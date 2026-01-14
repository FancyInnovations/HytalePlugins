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

import java.util.Collection;

public class TeleportAllCMD extends CommandBase {

    public TeleportAllCMD() {
        super("teleportall", "Teleports all players on the server to your location");
        addAliases("tpall");
        requirePermission("fancycore.commands.teleportall");
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

        // Get sender's location
        Ref<EntityStore> senderRef = ctx.senderAsPlayerRef();
        if (senderRef == null || !senderRef.isValid()) {
            ctx.sendMessage(Message.raw("You are not in a world."));
            return;
        }

        Store<EntityStore> senderStore = senderRef.getStore();
        World senderWorld = ((EntityStore) senderStore.getExternalData()).getWorld();

        // Get all players in the sender's world
        Collection<PlayerRef> playersToTeleport = senderWorld.getPlayerRefs();

        // Execute teleportation on the world thread
        senderWorld.execute(() -> {
            // Get sender's transform and rotation (must be on world thread)
            TransformComponent senderTransformComponent = (TransformComponent) senderStore.getComponent(senderRef, TransformComponent.getComponentType());
            if (senderTransformComponent == null) {
                fp.sendMessage("Failed to get your transform.");
                return;
            }

            HeadRotation senderHeadRotationComponent = (HeadRotation) senderStore.getComponent(senderRef, HeadRotation.getComponentType());
            if (senderHeadRotationComponent == null) {
                fp.sendMessage("Failed to get your head rotation.");
                return;
            }

            // Create transform from sender's location
            Transform destinationTransform = new Transform(
                    senderTransformComponent.getPosition().clone(),
                    senderHeadRotationComponent.getRotation().clone()
            );
            int count = 0;

            for (PlayerRef playerRef : playersToTeleport) {
                Ref<EntityStore> ref = playerRef.getReference();
                if (ref == null || !ref.isValid()) {
                    continue;
                }

                // Skip the sender
                if (ref.equals(senderRef)) {
                    continue;
                }

                Store<EntityStore> playerStore = ref.getStore();
                World playerWorld = ((EntityStore) playerStore.getExternalData()).getWorld();

                // Only teleport players in the same world as sender
                if (!playerWorld.equals(senderWorld)) {
                    continue;
                }

                // Create teleport component
                Teleport teleport = new Teleport(senderWorld, destinationTransform);

                // Add teleport component to player
                playerStore.addComponent(ref, Teleport.getComponentType(), teleport);
                count++;
            }

            // Send success message
            fp.sendMessage("Teleported " + count + " player(s) to your location.");
        });
    }
}
