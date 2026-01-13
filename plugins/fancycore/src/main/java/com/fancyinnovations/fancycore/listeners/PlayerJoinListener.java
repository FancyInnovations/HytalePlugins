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
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener {

    private final static FancyPlayerServiceImpl playerService = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();

    public static void onPlayerJoin(PlayerConnectEvent event) {

        System.out.println("player joined " + event.getPlayerRef().getUsername());

        // TODO (HTEA): use real event and register listener properly

        boolean firstJoin = false;

        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(event.getPlayerRef().getUuid());
        if (fp == null) {
            fp = new FancyPlayerImpl(
                    new FancyPlayerDataImpl(event.getPlayerRef().getUuid(), event.getPlayerRef().getUsername()),
                    event.getPlayerRef()
            );
            firstJoin = true;
        }

        Punishment punishment = fp.isBanned();
        if (punishment != null) {
            event.getPlayerRef().getPacketHandler().disconnect("You are banned from this server."); //TODO (I18N): replace with translated message (include ban reason and duration)
            return;
        }


        fp.setPlayer(event.getPlayerRef());

        if (firstJoin) {
            fp.setJoinedAt(System.currentTimeMillis());
            for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
                String firstJoinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getFirstJoinMessage());
                onlinePlayer.sendMessage(firstJoinMsg);
            }
            FancyCorePlugin.get().getPlayerStorage().savePlayer(fp.getData());
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
     * <p>
     * TODO (HTEA): remove this when using real event
     */
    interface PlayerJoinEvent {
        @NotNull FakeHytalePlayer getPlayer();

        void cancel();
    }

}
