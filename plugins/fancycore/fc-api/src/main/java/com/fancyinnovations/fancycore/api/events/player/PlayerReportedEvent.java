package com.fancyinnovations.fancycore.api.events.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.discord.Embed;
import com.fancyinnovations.fancycore.api.discord.Message;
import com.fancyinnovations.fancycore.api.moderation.PlayerReport;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.translations.MessageKey;

import java.util.List;

/**
 * Event fired when a player is reported by another player.
 */
public class PlayerReportedEvent extends PlayerEvent {

    private final PlayerReport report;

    public PlayerReportedEvent(FancyPlayer player, PlayerReport report) {
        super(player);
        this.report = report;
    }

    /**
     * Returns the report that was filed against the player.
     *
     * @return the PlayerReport object
     */
    public PlayerReport getReport() {
        return report;
    }

    @Override
    public Message getDiscordMessage() {
        String title = FancyCore.get().getTranslationService().getRaw(MessageKey.DISCORD_PLAYER_REPORTED);

        String embedTitle = FancyCore.get().getTranslationService()
                .getMessage(MessageKey.EVENT_PLAYER_REPORTED)
                .replace("player", player.getData().getUsername())
                .replace("reporter", report.reportingPlayer().getData().getUsername())
                .replace("reason", report.reason())
                .getParsedMessage();

        return new Message(
                title,
                List.of(
                        new Embed(
                                embedTitle,
                                "Reason: " + report.reason() +
                                        "\nReported by: " + report.reportingPlayer().getData().getUsername(),
                                0xdbb134
                        )
                )
        );
    }
}
