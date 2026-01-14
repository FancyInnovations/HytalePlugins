package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.TeleportRequestService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeleportAcceptCMD extends CommandBase {

    protected final OptionalArg<PlayerRef> senderArg = this.withOptionalArg("", "The player who sent the request", TeleportArg.TYPE);

    public TeleportAcceptCMD() {
        super("teleportaccept", "Accepts a pending teleport request from another player");
        addAliases("tpa", "tpaccept");
         requirePermission("fancycore.commands.teleportaccept");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer target = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (target == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        TeleportRequestService requestService = TeleportRequestService.get();
        UUID senderUUID;

        if (senderArg.provided(ctx)) {
            // Specific player provided
            PlayerRef senderPlayerRef = senderArg.get(ctx);
            FancyPlayer sender = FancyPlayerService.get().getByUUID(senderPlayerRef.getUuid());
            if (sender == null) {
                ctx.sendMessage(Message.raw("Sender player not found."));
                return;
            }

            senderUUID = requestService.getRequest(target, sender);
            if (senderUUID == null) {
                ctx.sendMessage(Message.raw("You do not have a pending teleport request from " + sender.getData().getUsername() + "."));
                return;
            }
        } else {
            // No player specified, get first request
            senderUUID = requestService.getFirstRequest(target);
            if (senderUUID == null) {
                ctx.sendMessage(Message.raw("You do not have any pending teleport requests."));
                return;
            }
        }

        FancyPlayer sender = FancyPlayerService.get().getByUUID(senderUUID);
        if (sender == null || !sender.isOnline()) {
            ctx.sendMessage(Message.raw("The player who sent the request is no longer online."));
            requestService.removeAllRequests(target);
            return;
        }

        PlayerRef senderPlayerRef = sender.getPlayer();
        if (senderPlayerRef == null) {
            ctx.sendMessage(Message.raw("The player who sent the request is no longer online."));
            requestService.removeRequest(target, sender);
            return;
        }

        Ref<EntityStore> senderRef = senderPlayerRef.getReference();
        if (senderRef == null || !senderRef.isValid()) {
            ctx.sendMessage(Message.raw("The player who sent the request is not in a world."));
            requestService.removeRequest(target, sender);
            return;
        }

        Ref<EntityStore> targetRef = ctx.senderAsPlayerRef();
        if (targetRef == null || !targetRef.isValid()) {
            ctx.sendMessage(Message.raw("You are not in a world."));
            return;
        }

        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = ((EntityStore) targetStore.getExternalData()).getWorld();
        Store<EntityStore> senderStore = senderRef.getStore();
        World senderWorld = ((EntityStore) senderStore.getExternalData()).getWorld();

        // Remove the request
        requestService.removeRequest(target, sender);

        // Save sender's previous location for /back command (on sender's world thread)
        senderWorld.execute(() -> {
            TeleportLocationHelper.savePreviousLocation(sender, senderRef, senderStore, senderWorld);
        });

        // Get target's location on the target world thread
        targetWorld.execute(() -> {
            // Get target's transform and rotation
            TransformComponent targetTransformComponent = (TransformComponent) targetStore.getComponent(targetRef, TransformComponent.getComponentType());
            if (targetTransformComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your transform."));
                return;
            }

            HeadRotation targetHeadRotationComponent = (HeadRotation) targetStore.getComponent(targetRef, HeadRotation.getComponentType());
            if (targetHeadRotationComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your head rotation."));
                return;
            }

            // Create transform from target's location
            Transform destinationTransform = new Transform(
                    targetTransformComponent.getPosition().clone(),
                    targetHeadRotationComponent.getRotation().clone()
            );

            // Now execute teleportation on the sender's world thread
            senderWorld.execute(() -> {

                // Create teleport component
                Teleport teleport = new Teleport(targetWorld, destinationTransform);

                // Add teleport component to sender
                senderStore.addComponent(senderRef, Teleport.getComponentType(), teleport);

                // Send success messages
                ctx.sendMessage(Message.raw("Accepted teleport request from " + sender.getData().getUsername() + "."));
                sender.sendMessage(target.getData().getUsername() + " accepted your teleport request.");
            });
        });
    }
}
