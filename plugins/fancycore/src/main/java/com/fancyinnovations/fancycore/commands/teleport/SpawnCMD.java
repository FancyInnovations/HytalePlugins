package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.SpawnLocation;
import com.fancyinnovations.fancycore.api.teleport.SpawnService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class SpawnCMD extends CommandBase {

    public SpawnCMD() {
        super("spawn", "Teleports you to the server's spawn point");
        requirePermission("fancycore.commands.spawn");
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

        // Get spawn location from storage
        SpawnLocation spawnLocation = SpawnService.get().getSpawnLocation();
        if (spawnLocation == null) {
            ctx.sendMessage(Message.raw("Spawn location has not been set. Use /setspawn to set it."));
            return;
        }

        // Get target world
        World targetWorld = Universe.get().getWorld(spawnLocation.worldName());
        if (targetWorld == null) {
            ctx.sendMessage(Message.raw("The spawn world \"" + spawnLocation.worldName() + "\" does not exists."));
            return;
        }

        // Save previous location for /back command (on current world thread)
        currentWorld.execute(() -> {
            TeleportLocationHelper.savePreviousLocation(fp, senderRef, senderStore, currentWorld);
        });

        // Execute teleportation on the target world thread
        targetWorld.execute(() -> {
            // Create teleport component
            Teleport teleport = new Teleport(targetWorld, spawnLocation.toTransform());

            // Add teleport component to sender
            senderStore.addComponent(senderRef, Teleport.getComponentType(), teleport);

            // Send success message
            ctx.sendMessage(Message.raw("Teleported to spawn."));
        });
    }
}
