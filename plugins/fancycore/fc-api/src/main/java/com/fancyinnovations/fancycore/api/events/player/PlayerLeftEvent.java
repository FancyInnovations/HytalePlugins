package com.fancyinnovations.fancycore.api.events.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.discord.Message;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.translations.MessageKey;

import java.util.List;

/**
 * Event fired when a player left the server.
 */
public class PlayerLeftEvent extends PlayerEvent {

    public PlayerLeftEvent(FancyPlayer player) {
        super(player);
    }

    @Override
    public Message getDiscordMessage() {
        String message = FancyCore.get().getTranslationService()
                .getMessage(MessageKey.EVENT_PLAYER_LEFT)
                .replace("player", player.getData().getUsername())
                .getParsedMessage();

        return new Message(message, List.of());
    }
}
