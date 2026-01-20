package com.fancyinnovations.fancycore.api.events.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.discord.Message;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.translations.MessageKey;

import java.util.List;

/**
 * Event fired when a player joins the server.
 */
public class PlayerJoinedEvent extends PlayerEvent {

    private boolean firstJoin;

    public PlayerJoinedEvent(FancyPlayer player, boolean firstJoin) {
        super(player);
        this.firstJoin = firstJoin;
    }

    /**
     * Returns whether this is the player's first time joining the server.
     *
     * @return true if this is the player's first join, false otherwise
     */
    public boolean isFirstJoin() {
        return firstJoin;
    }

    @Override
    public Message getDiscordMessage() {
        String message = FancyCore.get().getTranslationService()
                .getMessage(MessageKey.EVENT_PLAYER_JOINED)
                .replace("player", player.getData().getUsername())
                .getParsedMessage();

        return new Message(message, List.of());
    }
}
