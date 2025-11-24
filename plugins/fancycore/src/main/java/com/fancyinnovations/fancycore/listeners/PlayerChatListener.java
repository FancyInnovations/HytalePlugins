package com.fancyinnovations.fancycore.listeners;

import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.player.FakeHytalePlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import org.jetbrains.annotations.NotNull;

public class PlayerChatListener {

    private final static FancyPlayerServiceImpl playerService = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();

    public void onPlayerChat(PlayerChatEvent event) {
        // TODO: use real event and register listener properly

        FancyPlayer fp = playerService.getByUUID(event.getPlayer().getUUID());
        Punishment punishment = fp.isMuted();
        if (punishment != null) {
            event.cancel();
            fp.sendMessage("You are muted and cannot send messages."); //TODO: replace with translated message (include mute reason and duration)
        }
    }

    /**
     * Mock PlayerChatEvent for demonstration purposes.
     */
    interface PlayerChatEvent {
        @NotNull FakeHytalePlayer getPlayer();

        @NotNull String getMessage();

        void cancel();
    }
}
