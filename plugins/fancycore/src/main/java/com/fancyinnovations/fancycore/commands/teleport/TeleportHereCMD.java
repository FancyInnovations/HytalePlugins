package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class TeleportHereCMD extends CommandBase {

    protected final RequiredArg<PlayerRef> targetArg = this.withRequiredArg("", "The player to teleport", TeleportArg.TYPE);

    public TeleportHereCMD() {
        super("teleporthere", "Teleports the specified player to your location");
        addAliases("tphere");
        requirePermission("fancycore.commands.teleporthere");
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
            fp.sendMessage("You are not in a world.");
            return;
        }

        Store<EntityStore> senderStore = senderRef.getStore();
        World senderWorld = ((EntityStore) senderStore.getExternalData()).getWorld();

        // Get target player
        PlayerRef targetPlayerRef = targetArg.get(ctx);
        Ref<EntityStore> targetRef = targetPlayerRef.getReference();
        if (targetRef == null || !targetRef.isValid()) {
            fp.sendMessage("Target player is not in a world.");
            return;
        }

        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = ((EntityStore) targetStore.getExternalData()).getWorld();

        // First, get sender's location on the sender's world thread
        senderWorld.execute(() -> {
            // Get sender's transform and rotation
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

            // Now execute teleportation on the target world thread
            targetWorld.execute(() -> {
                // Save previous location for /back command
                FancyPlayer targetFp = FancyPlayerService.get().getByUUID(targetPlayerRef.getUuid());
                if (targetFp != null) {
                    TeleportLocationHelper.savePreviousLocation(targetFp, targetRef, targetStore, targetWorld);
                }

                // Create teleport component
                Teleport teleport = new Teleport(senderWorld, destinationTransform);

                // Add teleport component to target player
                targetStore.addComponent(targetRef, Teleport.getComponentType(), teleport);

                // Send success message
                fp.sendMessage("Teleported " + targetPlayerRef.getUsername() + " to your location.");
            });
        });
    }
}
