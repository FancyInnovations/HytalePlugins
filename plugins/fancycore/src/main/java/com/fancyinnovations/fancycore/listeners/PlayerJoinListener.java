package com.fancyinnovations.fancycore.listeners;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.events.player.PlayerJoinedEvent;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.teleport.Location;
import com.fancyinnovations.fancycore.api.teleport.SpawnService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.FancyPlayerDataImpl;
import com.fancyinnovations.fancycore.player.FancyPlayerImpl;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class PlayerJoinListener {

    private final static FancyPlayerServiceImpl playerService = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();

    public static void onPlayerConnect(PlayerConnectEvent event) {
        boolean firstJoin = false;

        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(event.getPlayerRef().getUuid());
        if (fp == null) {
            fp = new FancyPlayerImpl(
                    new FancyPlayerDataImpl(event.getPlayerRef().getUuid(), event.getPlayerRef().getUsername()),
                    event.getPlayerRef()
            );
            firstJoin = true;
        }
        fp.setPlayer(event.getPlayerRef());

        fp.getCurrentChatRoom(); // Ensure default chat room is set

        Punishment punishment = fp.isBanned();
        if (punishment != null) {
            event.getPlayerRef().getPacketHandler().disconnect("You are banned from this server."); //TODO (I18N): replace with translated message (include ban reason and duration)
            return;
        }

        if (firstJoin) {
            fp.setJoinedAt(System.currentTimeMillis());
            for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
                String firstJoinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getFirstJoinMessage());
                onlinePlayer.sendMessage(firstJoinMsg);
            }
            FancyCorePlugin.get().getPlayerStorage().savePlayer(fp.getData());

            String joinMessage = PlaceholderService.get().parse(fp, FancyCorePlugin.get().getConfig().getFirstJoinMessage());
            fp.sendMessage(joinMessage);
        } else {
            String joinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getJoinMessage());
            for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
                onlinePlayer.sendMessage(joinMsg);
            }
        }

        playerService.addOnlinePlayer(fp);
        new PlayerJoinedEvent(fp, firstJoin).fire();
    }

    public static void onPlayerReady(PlayerReadyEvent event) {
        Ref<EntityStore> ref = event.getPlayerRef();
        Store<EntityStore> store = event.getPlayerRef().getStore();

        UUIDComponent uuidComponent = ref.getStore().getComponent(ref, UUIDComponent.getComponentType());
        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(uuidComponent.getUuid());
        if (fp == null) {
            return;
        }

        if (FancyCorePlugin.get().getConfig().shouldJoinAtSpawn()) {
            Location spawnLocation = SpawnService.get().getSpawnLocation();
            if (spawnLocation != null) {
                Transform spawn = SpawnService.get().getSpawnLocation().toTransform();
                TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());

                Vector3f previousBodyRotation = transformComponent.getRotation().clone();
                Vector3f spawnRotation = spawn.getRotation().clone();
                spawn.setRotation(new Vector3f(previousBodyRotation.getPitch(), spawnRotation.getYaw(), previousBodyRotation.getRoll()));

                Teleport teleport = new Teleport(event.getPlayer().getWorld(), spawn).withHeadRotation(spawnRotation);
                store.addComponent(ref, Teleport.getComponentType(), teleport);
            }
        }

        String joinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getJoinMessage());
        fp.sendMessage(joinMsg);
    }

    public static void onAddPlayerToWorld(AddPlayerToWorldEvent event) {
        event.setBroadcastJoinMessage(false);
    }
}
