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
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HomeCMD extends CommandBase {

    protected final OptionalArg<String> nameArg = this.withOptionalArg("", "Home name", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);

    public HomeCMD() {
        super("home", "Teleports you to your home point with the specified name or the first home if no name is provided");
        addAliases("homes", "h");
        requirePermission("fancycore.commands.home");
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

        Store<EntityStore> senderStore = senderRef.getStore();
        World currentWorld = ((EntityStore) senderStore.getExternalData()).getWorld();

        // Get homes map
        Map<String, Object> customData = fp.getData().getCustomData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> homes = (Map<String, Map<String, Object>>) customData.get("homes");

        if (homes == null || homes.isEmpty()) {
            ctx.sendMessage(Message.raw("You do not have any homes set. Use /sethome <name> to set a home."));
            return;
        }

        // Determine which home to teleport to
        Map<String, Object> homeLocation;
        final String homeName;

        if (nameArg.provided(ctx)) {
            String providedName = nameArg.get(ctx);
            if (providedName == null || providedName.trim().isEmpty()) {
                // If empty string provided, treat as no argument
                homeName = homes.keySet().iterator().next();
                homeLocation = homes.get(homeName);
            } else {
                homeName = providedName.trim();
                homeLocation = homes.get(homeName);
                if (homeLocation == null) {
                    ctx.sendMessage(Message.raw("Home \"" + homeName + "\" does not exist."));
                    return;
                }
            }
        } else {
            // Get first home
            homeName = homes.keySet().iterator().next();
            homeLocation = homes.get(homeName);
        }

        // Extract home location data
        String worldName = (String) homeLocation.get("world");
        Double x = ((Number) homeLocation.get("x")).doubleValue();
        Double y = ((Number) homeLocation.get("y")).doubleValue();
        Double z = ((Number) homeLocation.get("z")).doubleValue();
        Double yaw = ((Number) homeLocation.get("yaw")).doubleValue();
        Double pitch = ((Number) homeLocation.get("pitch")).doubleValue();

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
            // Create transform from home location
            Transform destinationTransform = new Transform(
                    new Vector3d(x, y, z),
                    new Vector3f((float) pitch.doubleValue(), (float) yaw.doubleValue(), 0.0f)
            );

            // Create teleport component
            Teleport teleport = new Teleport(targetWorld, destinationTransform);

            // Add teleport component to sender
            senderStore.addComponent(senderRef, Teleport.getComponentType(), teleport);

            // Send success message
            if (nameArg.provided(ctx)) {
                ctx.sendMessage(Message.raw("Teleported to home \"" + homeName + "\"."));
            } else {
                ctx.sendMessage(Message.raw("Teleported to home \"" + homeName + "\"."));
            }
        });
    }
}
