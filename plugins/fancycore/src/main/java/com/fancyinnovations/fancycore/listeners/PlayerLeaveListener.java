package com.fancyinnovations.fancycore.listeners;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.events.player.PlayerLeftEvent;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.FancyPlayerImpl;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;

import java.util.concurrent.TimeUnit;

public class PlayerLeaveListener {

    private final static FancyPlayerServiceImpl playerService = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();

    public static void onPlayerLeave(PlayerDisconnectEvent event) {
        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(event.getPlayerRef().getUuid());
        if (fp == null) {
            return;
        }

        if (fp.getJoinedAt() != -1) {
            long playtime = System.currentTimeMillis() - fp.getJoinedAt();
            fp.getData().addPlayTime(playtime);
            fp.setJoinedAt(-1);
        }

        FancyCore.get().getPlayerStorage().savePlayer(fp.getData());

        String leaveMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getLeaveMessage());
        for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
            onlinePlayer.sendMessage(leaveMsg);
        }

        FancyCore.get().getThreadPool().schedule(
                () -> FancyCorePlugin.get().getScoreboardServiceImpl().detachScoreboard(fp),
                50, TimeUnit.MILLISECONDS   // ~1 tick (50ms)
        );

        new PlayerLeftEvent(fp).fire();
        fp.setPlayer(null);
    }
}
