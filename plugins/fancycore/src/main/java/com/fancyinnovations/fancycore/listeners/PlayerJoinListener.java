package com.fancyinnovations.fancycore.listeners;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.FancyPlayerDataImpl;
import com.fancyinnovations.fancycore.player.FancyPlayerImpl;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

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
    }

    public static void onPlayerReady(PlayerReadyEvent event) {
        UUIDComponent uuidComponent = event.getPlayerRef().getStore().getComponent(event.getPlayerRef(), UUIDComponent.getComponentType());
        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(uuidComponent.getUuid());
        if (fp == null) {
            return;
        }

        String joinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getJoinMessage());
        fp.sendMessage(joinMsg);
    }

    public static void onAddPlayerToWorld(AddPlayerToWorldEvent event) {
        event.setBroadcastJoinMessage(false);
    }
}
