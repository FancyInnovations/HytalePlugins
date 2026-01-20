package com.fancyinnovations.fancycore.api.events.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.discord.Embed;
import com.fancyinnovations.fancycore.api.discord.Message;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.translations.MessageKey;

import java.util.List;

/**
 * Event fired when a player receives a punishment.
 */
public class PlayerPunishedEvent extends PlayerEvent {

    private final Punishment punishment;

    public PlayerPunishedEvent(FancyPlayer player, Punishment punishment) {
        super(player);
        this.punishment = punishment;
    }

    /**
     * Returns the punishment that was applied to the player.
     *
     * @return the Punishment object
     */
    public Punishment getPunishment() {
        return punishment;
    }

    private String getDuration() {
        return punishment.expiresAt() == -1 ? "Permanent" : ((punishment.expiresAt() - punishment.issuedAt()) / 1000) + " seconds";
    }

    @Override
    public Message getDiscordMessage() {
        var translationService = FancyCore.get().getTranslationService();

        switch (punishment.type()) {
            case WARNING -> {
                String title = translationService.getRaw(MessageKey.DISCORD_PLAYER_WARNED);
                String embedContent = translationService
                        .getMessage(MessageKey.EVENT_PLAYER_PUNISHED_WARNING)
                        .replace("player", player.getData().getUsername())
                        .replace("reason", punishment.reason())
                        .replace("issued_by", punishment.issuedBy().toString())
                        .getParsedMessage();

                return new Message(
                        title,
                        List.of(new Embed(embedContent, "Reason: " + punishment.reason() + "\nIssued by: " + punishment.issuedBy(), 0xdbb134))
                );
            }

            case MUTE -> {
                String title = translationService.getRaw(MessageKey.DISCORD_PLAYER_MUTED);
                String embedContent = translationService
                        .getMessage(MessageKey.EVENT_PLAYER_PUNISHED_MUTE)
                        .replace("player", player.getData().getUsername())
                        .replace("reason", punishment.reason())
                        .replace("issued_by", punishment.issuedBy().toString())
                        .replace("duration", getDuration())
                        .getParsedMessage();

                return new Message(
                        title,
                        List.of(new Embed(embedContent, "Reason: " + punishment.reason() + "\nIssued by: " + punishment.issuedBy() + "\nDuration: " + getDuration(), 0xdbb134))
                );
            }

            case KICK -> {
                String title = translationService.getRaw(MessageKey.DISCORD_PLAYER_KICKED);
                String embedContent = translationService
                        .getMessage(MessageKey.EVENT_PLAYER_PUNISHED_KICK)
                        .replace("player", player.getData().getUsername())
                        .replace("reason", punishment.reason())
                        .replace("issued_by", punishment.issuedBy().toString())
                        .getParsedMessage();

                return new Message(
                        title,
                        List.of(new Embed(embedContent, "Reason: " + punishment.reason() + "\nIssued by: " + punishment.issuedBy(), 0xdbb134))
                );
            }

            case BAN -> {
                String title = translationService.getRaw(MessageKey.DISCORD_PLAYER_BANNED);
                String embedContent = translationService
                        .getMessage(MessageKey.EVENT_PLAYER_PUNISHED_BAN)
                        .replace("player", player.getData().getUsername())
                        .replace("reason", punishment.reason())
                        .replace("issued_by", punishment.issuedBy().toString())
                        .replace("duration", getDuration())
                        .getParsedMessage();

                return new Message(
                        title,
                        List.of(new Embed(embedContent, "Reason: " + punishment.reason() + "\nIssued by: " + punishment.issuedBy() + "\nDuration: " + getDuration(), 0xdbb134))
                );
            }
        }

        return super.getDiscordMessage();
    }
}
