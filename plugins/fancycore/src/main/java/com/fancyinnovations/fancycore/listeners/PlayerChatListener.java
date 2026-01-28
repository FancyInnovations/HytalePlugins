package com.fancyinnovations.fancycore.listeners;

import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;

public class PlayerChatListener {

    private final static FancyPlayerServiceImpl playerService = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();

    public static void onPlayerChat(PlayerChatEvent event) {
        event.setCancelled(true);

        FancyPlayer fp = playerService.getByUUID(event.getSender().getUuid());
        Punishment punishment = fp.isMuted();
        if (punishment != null) {
            if (punishment.expiresAt() < 0) {
                fp.sendMessage("You are muted and cannot send messages.");
            } else {
                String duration = TimeUtils.formatTime(punishment.remainingDuration());
                fp.sendMessage("You are still muted for " + duration + " and cannot send messages.");
            }
            return;
        }

        fp.getCurrentChatRoom().sendMessage(fp, event.getContent());
    }

}
