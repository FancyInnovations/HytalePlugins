package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerDeathPositionData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeleportDeathBackCMD extends CommandBase {

    public TeleportDeathBackCMD() {
        super("deathback", "Teleports you to the location where you last died");
        addAliases("deathtp", "deathteleport");
        requirePermission("fancycore.commands.deathback");
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

        // Get death position from PlayerWorldData
        currentWorld.execute(() -> {
            Player playerComponent = (Player) senderStore.getComponent(senderRef, Player.getComponentType());
            if (playerComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get player component."));
                return;
            }

            PlayerWorldData perWorldData = playerComponent.getPlayerConfigData().getPerWorldData(currentWorld.getName());
            List<PlayerDeathPositionData> deathPositions = perWorldData.getDeathPositions();

            if (deathPositions == null || deathPositions.isEmpty()) {
                ctx.sendMessage(Message.raw("You have not died in this world."));
                return;
            }

            // Get the most recent death (last in the list)
            PlayerDeathPositionData lastDeath = deathPositions.get(deathPositions.size() - 1);
            Transform deathTransform = lastDeath.getTransform();

            // The death position is in the current world (perWorldData is world-specific)
            World targetWorld = currentWorld;

            // Execute teleportation on the target world thread
            targetWorld.execute(() -> {
                // Create teleport component from death position
                Teleport teleport = new Teleport(targetWorld, deathTransform);

                // Add teleport component to sender
                senderStore.addComponent(senderRef, Teleport.getComponentType(), teleport);

                // Send success message
                ctx.sendMessage(Message.raw("Teleported to your last death location."));
            });
        });
    }
}
