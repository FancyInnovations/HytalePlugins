package com.fancyinnovations.fancycore.api.events.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.discord.Embed;
import com.fancyinnovations.fancycore.api.discord.Message;
import com.fancyinnovations.fancycore.api.events.FancyEvent;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.translations.MessageKey;

import java.util.List;

/**
 * The base class for all player-related events in the FancyCore system.
 */
public abstract class PlayerEvent extends FancyEvent {

    protected final FancyPlayer player;

    public PlayerEvent(FancyPlayer player) {
        super();
        this.player = player;
    }

    /**
     * Returns the player associated with this event.
     *
     * @return the FancyPlayer involved in the event
     */
    public FancyPlayer getPlayer() {
        return player;
    }

    @Override
    public Message getDiscordMessage() {
        String message = FancyCore.get().getTranslationService()
                .getMessage(MessageKey.EVENT_PLAYER_GENERIC)
                .replace("player", player.getData().getUsername())
                .getParsedMessage();

        String embedTitle = FancyCore.get().getTranslationService().getRaw(MessageKey.DISCORD_PLAYER_EVENT_FIRED);

        return new Message(
                message,
                List.of(
                        new Embed(
                                embedTitle,
                                "Event Type: " + this.getClass().getSimpleName() + "\nFired At: <t:"+firedAt()+":f>" +
                                        "\nPlayer: " + player.getData().getUsername(),
                                0x3498db
                        )
                )
        );
    }
}
