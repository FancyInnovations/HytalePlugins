package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WarpCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("", "Warp name", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);

    public WarpCMD() {
        super("warp", "Teleports you to the warp point with the specified name");
        // TODO: Permission check
        // requirePermission("fancycore.commands.warp");
        // Note: Per-warp permission should be checked as: fancycore.commands.warp.<name>
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

        String warpName = nameArg.get(ctx);
        if (warpName == null || warpName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Warp name cannot be empty."));
            return;
        }

        // TODO: Check per-warp permission: fancycore.commands.warp.<name>
        // if (!ctx.sender().hasPermission("fancycore.commands.warp." + warpName)) {
        //     ctx.sendMessage(Message.raw("You do not have permission to use this warp."));
        //     return;
        // }

        Store<EntityStore> senderStore = senderRef.getStore();
        World currentWorld = ((EntityStore) senderStore.getExternalData()).getWorld();

        // Get warp location
        com.fancyinnovations.fancycore.teleport.storage.WarpStorage warpStorage = 
                com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getWarpStorage();
        Map<String, Object> warpLocation = warpStorage.getWarp(warpName);

        if (warpLocation == null) {
            ctx.sendMessage(Message.raw("Warp \"" + warpName + "\" does not exist."));
            return;
        }

        // Extract warp location data
        String worldName = (String) warpLocation.get("world");
        Double x = ((Number) warpLocation.get("x")).doubleValue();
        Double y = ((Number) warpLocation.get("y")).doubleValue();
        Double z = ((Number) warpLocation.get("z")).doubleValue();
        Double yaw = ((Number) warpLocation.get("yaw")).doubleValue();
        Double pitch = ((Number) warpLocation.get("pitch")).doubleValue();

        // Get target world
        World targetWorld = Universe.get().getWorld(worldName);
        if (targetWorld == null) {
            ctx.sendMessage(Message.raw("The world \"" + worldName + "\" is no longer loaded."));
            return;
        }

        // Save previous location for /back command (on current world thread)
        currentWorld.execute(() -> {
            TeleportLocationHelper.savePreviousLocation(fp, senderRef, senderStore, currentWorld);
        });

        // Execute teleportation on the target world thread
        targetWorld.execute(() -> {
            // Create transform from warp location
            Transform destinationTransform = new Transform(
                    new Vector3d(x, y, z),
                    new Vector3f((float) pitch.doubleValue(), (float) yaw.doubleValue(), 0.0f)
            );

            // Create teleport component
            Teleport teleport = new Teleport(targetWorld, destinationTransform);

            // Add teleport component to sender
            senderStore.addComponent(senderRef, Teleport.getComponentType(), teleport);

            // Send success message
            ctx.sendMessage(Message.raw("Teleported to warp \"" + warpName + "\"."));
        });
    }
}
