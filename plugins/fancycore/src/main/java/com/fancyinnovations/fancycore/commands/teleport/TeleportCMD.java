package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class TeleportCMD extends CommandBase {

    protected final RequiredArg<PlayerRef> targetArg = this.withRequiredArg(TeleportArg.NAME, TeleportArg.DESCRIPTION, TeleportArg.TYPE);
    protected final OptionalArg<PlayerRef> destinationArg = this.withOptionalArg("destination", "The destination player", TeleportArg.TYPE);

    public TeleportCMD() {
        super("tp", "Teleports you or the specified player to another player's location");
        addAliases("teleport");
        // TODO: Permission check
        // requirePermission("fancycore.commands.tp");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        final PlayerRef targetPlayerRef;
        final Ref<EntityStore> targetRef;
        final PlayerRef destinationPlayerRef;
        final boolean isTwoArg;

        if (destinationArg.provided(ctx)) {
            // Two arguments: teleport target to destination
            isTwoArg = true;
            targetPlayerRef = targetArg.get(ctx);
            targetRef = targetPlayerRef.getReference();
            if (targetRef == null || !targetRef.isValid()) {
                ctx.sendMessage(Message.raw("Target player is not in a world."));
                return;
            }
            destinationPlayerRef = destinationArg.get(ctx);
        } else {
            // One argument: teleport sender to target (requires player sender)
            isTwoArg = false;
            if (!ctx.isPlayer()) {
                ctx.sendMessage(Message.raw("This command requires a destination player when executed from console. Usage: /tp <target> <destination>"));
                return;
            }

            if (!ctx.isPlayer()) {
                ctx.sendMessage(Message.raw("This command can only be executed by a player."));
                return;
            }

            FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
            if (fp == null) {
                fp.sendMessage("FancyPlayer not found.");
                return;
            }

            PlayerRef senderPlayerRef = fp.getPlayer();
            if (senderPlayerRef == null) {
                fp.sendMessage("You are not online.");
                return;
            }

            // For single arg, we teleport sender to target
            targetPlayerRef = senderPlayerRef;
            targetRef = targetPlayerRef.getReference();
            if (targetRef == null || !targetRef.isValid()) {
                fp.sendMessage("You are not in a world.");
                return;
            }
            destinationPlayerRef = targetArg.get(ctx);
        }

        Ref<EntityStore> destinationRef = destinationPlayerRef.getReference();
        if (destinationRef == null || !destinationRef.isValid()) {
            ctx.sendMessage(Message.raw("Destination player is not in a world."));
            return;
        }

        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = ((EntityStore) targetStore.getExternalData()).getWorld();
        Store<EntityStore> destinationStore = destinationRef.getStore();
        World destinationWorld = ((EntityStore) destinationStore.getExternalData()).getWorld();

        // If target and destination are in the same world, we can do everything in one execute block
        if (targetWorld.equals(destinationWorld)) {
            // Execute teleportation on the world thread
            targetWorld.execute(() -> {
                // Get destination position and rotation
                TransformComponent destinationTransformComponent = (TransformComponent) destinationStore.getComponent(destinationRef, TransformComponent.getComponentType());
                if (destinationTransformComponent == null) {
                    ctx.sendMessage(Message.raw("Failed to get destination transform."));
                    return;
                }

                HeadRotation destinationHeadRotationComponent = (HeadRotation) destinationStore.getComponent(destinationRef, HeadRotation.getComponentType());
                if (destinationHeadRotationComponent == null) {
                    ctx.sendMessage(Message.raw("Failed to get destination head rotation."));
                    return;
                }

                // Create transform from destination
                Transform destinationTransform = new Transform(
                        destinationTransformComponent.getPosition().clone(),
                        destinationHeadRotationComponent.getRotation().clone()
                );

                // Create teleport component
                Teleport teleport = new Teleport(destinationWorld, destinationTransform);

                // Add teleport component to target player
                targetStore.addComponent(targetRef, Teleport.getComponentType(), teleport);

                // Send success message
                if (isTwoArg) {
                    ctx.sendMessage(Message.raw("Teleported " + targetPlayerRef.getUsername() + " to " + destinationPlayerRef.getUsername() + "."));
                } else {
                    ctx.sendMessage(Message.raw("Teleported to " + destinationPlayerRef.getUsername() + "."));
                }
            });
        } else {
            // Different worlds: get destination data first, then teleport
            destinationWorld.execute(() -> {
                // Get destination position and rotation
                TransformComponent destinationTransformComponent = (TransformComponent) destinationStore.getComponent(destinationRef, TransformComponent.getComponentType());
                if (destinationTransformComponent == null) {
                    ctx.sendMessage(Message.raw("Failed to get destination transform."));
                    return;
                }

                HeadRotation destinationHeadRotationComponent = (HeadRotation) destinationStore.getComponent(destinationRef, HeadRotation.getComponentType());
                if (destinationHeadRotationComponent == null) {
                    ctx.sendMessage(Message.raw("Failed to get destination head rotation."));
                    return;
                }

                // Create transform from destination
                Transform destinationTransform = new Transform(
                        destinationTransformComponent.getPosition().clone(),
                        destinationHeadRotationComponent.getRotation().clone()
                );

                // Now execute teleportation on the target world thread
                targetWorld.execute(() -> {
                    // Create teleport component
                    Teleport teleport = new Teleport(destinationWorld, destinationTransform);

                    // Add teleport component to target player
                    targetStore.addComponent(targetRef, Teleport.getComponentType(), teleport);

                    // Send success message
                    if (isTwoArg) {
                        ctx.sendMessage(Message.raw("Teleported " + targetPlayerRef.getUsername() + " to " + destinationPlayerRef.getUsername() + "."));
                    } else {
                        ctx.sendMessage(Message.raw("Teleported to " + destinationPlayerRef.getUsername() + "."));
                    }
                });
            });
        }
    }
}
