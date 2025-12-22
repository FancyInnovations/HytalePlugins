package com.fancyinnovations.fancycore.listeners;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FakeHytalePlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.FancyPlayerDataImpl;
import com.fancyinnovations.fancycore.player.FancyPlayerImpl;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener {

    private final static FancyPlayerServiceImpl playerService = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();

    public void onPlayerJoin(PlayerJoinEvent event) {
        // TODO: use real event and register listener properly

        boolean firstJoin = false;

        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(event.getPlayer().getUUID());
        if (fp == null) {
            fp = new FancyPlayerImpl(
                    new FancyPlayerDataImpl(event.getPlayer().getUUID(), event.getPlayer().getUsername()),
                    event.getPlayer()
            );
            firstJoin = true;
        }

        Punishment punishment = fp.isBanned();
        if (punishment != null) {
            event.cancel();
            event.getPlayer().kick("You are banned from this server."); //TODO: replace with translated message (include ban reason and duration)
            return;
        }


        fp.setPlayer(event.getPlayer());

        if (firstJoin) {
            fp.setJoinedAt(System.currentTimeMillis());
            for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
                String firstJoinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getFirstJoinMessage());
                onlinePlayer.sendMessage(firstJoinMsg);
            }
        } else {
            String joinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getJoinMessage());
            for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
                onlinePlayer.sendMessage(joinMsg);
            }
        }

        playerService.addOnlinePlayer(fp);
    }

    /**
     * Mock PlayerJoinEvent for demonstration purposes.
     */
    interface PlayerJoinEvent {
        @NotNull FakeHytalePlayer getPlayer();

        void cancel();
    }

}
