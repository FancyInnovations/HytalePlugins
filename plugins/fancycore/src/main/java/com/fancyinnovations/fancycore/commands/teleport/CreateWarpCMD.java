package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class CreateWarpCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("", "Warp name", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);

    public CreateWarpCMD() {
        super("createwarp", "Creates a warp point at your current location with the specified name");
         requirePermission("fancycore.commands.createwarp");
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

        String warpName = nameArg.get(ctx);
        if (warpName == null || warpName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Warp name cannot be empty."));
            return;
        }

        // Check if warp already exists
        com.fancyinnovations.fancycore.teleport.storage.WarpStorage warpStorage = 
                com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getWarpStorage();
        if (warpStorage.warpExists(warpName)) {
            ctx.sendMessage(Message.raw("A warp with the name \"" + warpName + "\" already exists."));
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

        // Execute on the world thread to get location
        senderWorld.execute(() -> {
            // Get sender's transform and rotation
            TransformComponent senderTransformComponent = (TransformComponent) senderStore.getComponent(senderRef, TransformComponent.getComponentType());
            if (senderTransformComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your transform."));
                return;
            }

            HeadRotation senderHeadRotationComponent = (HeadRotation) senderStore.getComponent(senderRef, HeadRotation.getComponentType());
            if (senderHeadRotationComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your head rotation."));
                return;
            }

            // Save warp
            warpStorage.setWarp(
                    warpName,
                    senderWorld.getName(),
                    senderTransformComponent.getPosition().getX(),
                    senderTransformComponent.getPosition().getY(),
                    senderTransformComponent.getPosition().getZ(),
                    senderHeadRotationComponent.getRotation().getYaw(),
                    senderHeadRotationComponent.getRotation().getPitch()
            );

            // Send success message
            ctx.sendMessage(Message.raw("Warp \"" + warpName + "\" created at your current location."));
        });
    }
}
