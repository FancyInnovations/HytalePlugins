package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NearCMD extends AbstractPlayerCommand {

    protected final OptionalArg<Double> radiusArg = this.withOptionalArg("radius", "Search radius (default: 50)", ArgTypes.DOUBLE);

    public NearCMD() {
        super("near", "Lists players near the command sender within a specified radius");
        requirePermission("fancycore.commands.near");
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        double radius = radiusArg.provided(ctx) ? radiusArg.get(ctx) : 50.0;
        if (radius < 0) {
            fp.sendMessage("Radius must be positive.");
            return;
        }

        world.execute(() -> {
            TransformComponent senderTransform = store.getComponent(ref, TransformComponent.getComponentType());
            if (senderTransform == null) {
                fp.sendMessage("Failed to get your position.");
                return;
            }

            Vector3d senderPos = senderTransform.getPosition();
            Collection<PlayerRef> allPlayers = world.getPlayerRefs();
            List<NearbyPlayer> nearbyPlayers = new ArrayList<>();

            for (PlayerRef otherPlayerRef : allPlayers) {
                // Skip self
                if (otherPlayerRef.getUuid().equals(playerRef.getUuid())) {
                    continue;
                }

                Ref<EntityStore> otherRef = otherPlayerRef.getReference();
                if (otherRef == null || !otherRef.isValid()) {
                    continue;
                }

                Store<EntityStore> otherStore = otherRef.getStore();
                World otherWorld = ((EntityStore) otherStore.getExternalData()).getWorld();
                if (!otherWorld.equals(world)) {
                    continue;
                }

                TransformComponent otherTransform = otherStore.getComponent(otherRef, TransformComponent.getComponentType());
                if (otherTransform == null) {
                    continue;
                }

                Vector3d otherPos = otherTransform.getPosition();
                double distance = senderPos.distanceTo(otherPos);

                if (distance <= radius) {
                    nearbyPlayers.add(new NearbyPlayer(otherPlayerRef.getUsername(), distance));
                }
            }

            // Sort by distance
            nearbyPlayers.sort((a, b) -> Double.compare(a.distance, b.distance));

            if (nearbyPlayers.isEmpty()) {
                fp.sendMessage("No players found within " + String.format("%.1f", radius) + " blocks.");
            } else {
                fp.sendMessage("Players within " + String.format("%.1f", radius) + " blocks (" + nearbyPlayers.size() + "):");
                for (NearbyPlayer np : nearbyPlayers) {
                    fp.sendMessage("  - " + np.username + " (" + String.format("%.1f", np.distance) + " blocks away)");
                }
            }
        });
    }

    private static class NearbyPlayer {
        final String username;
        final double distance;

        NearbyPlayer(String username, double distance) {
            this.username = username;
            this.distance = distance;
        }
    }
}
