package com.fancyinnovations.fancycore.listeners;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FakeHytalePlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.FancyPlayerImpl;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveListener {

    private final static FancyPlayerServiceImpl playerService = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();

    public void onPlayerLeave(PlayerLeaveEvent event) {
        // TODO: use real event and register listener properly

        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(event.getPlayer().getUUID());
        fp.setPlayer(null);

        long playtime = System.currentTimeMillis() - fp.getJoinedAt();
        fp.getData().addPlayTime(playtime);
        fp.setJoinedAt(-1);

        playerService.removeOnlinePlayer(fp);

        String joinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getLeaveMessage());
        for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
            onlinePlayer.sendMessage(joinMsg);
        }
    }

    /**
     * Mock PlayerLeaveEvent for demonstration purposes.
     */
    interface PlayerLeaveEvent {
        @NotNull FakeHytalePlayer getPlayer();
    }

}
