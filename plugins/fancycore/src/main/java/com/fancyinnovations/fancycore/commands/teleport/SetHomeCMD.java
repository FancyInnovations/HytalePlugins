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

import java.util.HashMap;
import java.util.Map;

public class SetHomeCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("", "Home name", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);

    public SetHomeCMD() {
        super("sethome", "Sets your home point to your current location");
        // TODO: Permission check
        // requirePermission("fancycore.commands.sethome");
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

        String homeName = nameArg.get(ctx);
        if (homeName == null || homeName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Home name cannot be empty."));
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

            // Get or create homes map
            Map<String, Object> customData = fp.getData().getCustomData();
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> homes = (Map<String, Map<String, Object>>) customData.get("homes");
            if (homes == null) {
                homes = new HashMap<>();
                customData.put("homes", homes);
            }

            // Create home location data
            Map<String, Object> homeLocation = new HashMap<>();
            homeLocation.put("world", senderWorld.getName());
            homeLocation.put("x", senderTransformComponent.getPosition().getX());
            homeLocation.put("y", senderTransformComponent.getPosition().getY());
            homeLocation.put("z", senderTransformComponent.getPosition().getZ());
            homeLocation.put("yaw", senderHeadRotationComponent.getRotation().getYaw());
            homeLocation.put("pitch", senderHeadRotationComponent.getRotation().getPitch());

            // Save home
            homes.put(homeName, homeLocation);
            fp.getData().setCustomData("homes", homes);

            // Send success message
            ctx.sendMessage(Message.raw("Home \"" + homeName + "\" set to your current location."));
        });
    }
}
