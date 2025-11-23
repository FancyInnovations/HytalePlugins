package com.fancyinnovations.fancycore.punishments.service;

import com.fancyinnovations.fancycore.api.events.player.PlayerPunishedEvent;
import com.fancyinnovations.fancycore.api.events.player.PlayerReportedEvent;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.punishments.Punishment;
import com.fancyinnovations.fancycore.api.punishments.PunishmentService;
import com.fancyinnovations.fancycore.api.punishments.PunishmentStorage;
import com.fancyinnovations.fancycore.api.punishments.PunishmentType;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.punishments.PlayerReportImpl;
import com.fancyinnovations.fancycore.punishments.PunishmentImpl;
import com.fancyinnovations.fancycore.translations.TranslationService;

import java.util.List;
import java.util.UUID;

public class PunishmentServiceImpl implements PunishmentService {

    private final PunishmentStorage storage;
    private final TranslationService translator;

    public PunishmentServiceImpl() {
        this.storage = FancyCorePlugin.get().getPunishmentStorage();
        this.translator = FancyCorePlugin.get().getTranslationService();

        translator
                .addMessage("punishments.warning.default_reason", "You have been warned for: {reason}.")
                .addMessage("punishments.warning.success", "Successfully warned {player} for: {reason}.")

                .addMessage("punishments.mute.perm.default_reason", "You have been muted for: {reason}.")
                .addMessage("punishments.mute.perm.success", "Successfully muted {player} for: {reason}.")
                .addMessage("punishments.mute.temp.default_reason", "You have been temporarily muted for: {reason}. Duration: {duration}.")
                .addMessage("punishments.mute.temp.success", "Successfully temporarily muted {player} for: {reason}. Duration: {duration}.")

                .addMessage("punishments.kick.default_reason", "You have been kicked from the server for: {reason}.")
                .addMessage("punishments.kick.success", "Successfully kicked {player} for: {reason}.")

                .addMessage("punishments.ban.perm.default_reason", "You have been banned from the server for: {reason}.")
                .addMessage("punishments.ban.perm.success", "Successfully banned {player} for: {reason}.")
                .addMessage("punishments.ban.temp.default_reason", "You have been temporarily banned from the server for: {reason}. Duration: {duration}.")
                .addMessage("punishments.ban.temp.success", "Successfully temporarily banned {player} for: {reason}. Duration: {duration}.")

                .addMessage("punishments.report.successfully_reported", "You have reported {reported} for: {reason}.\nOur staff will review your report soon.")
        ;
    }

    @Override
    public Punishment warnPlayer(FancyPlayer player, FancyPlayer staff, String reason) {
        Punishment punishment = new PunishmentImpl(
                UUID.randomUUID(),
                player.getUUID(),
                PunishmentType.WARNING,
                reason,
                System.currentTimeMillis(),
                staff.getUUID(),
                -1
        );

        if (!new PlayerPunishedEvent(player, punishment).fire()) {
            return null;
        }

        storage.createPunishment(punishment);

        translator.getMessage("punishments.warning.default_reason")
                .replace("{reason}", reason)
                .sendTo(player);

        translator.getMessage("punishments.warning.success")
                .replace("{player}", player.getUsername())
                .replace("{reason}", reason)
                .sendTo(staff);

        return punishment;
    }

    @Override
    public Punishment mutePlayer(FancyPlayer player, FancyPlayer staff, String reason, long durationMillis) {
        long expiresAt = durationMillis <= 0 ? -1 : System.currentTimeMillis() + durationMillis;

        Punishment punishment = new PunishmentImpl(
                UUID.randomUUID(),
                player.getUUID(),
                PunishmentType.MUTE,
                reason,
                System.currentTimeMillis(),
                staff.getUUID(),
                expiresAt
        );

        if (!new PlayerPunishedEvent(player, punishment).fire()) {
            return null;
        }

        storage.createPunishment(punishment);

        if (durationMillis > 0) {
            translator.getMessage("punishments.mute.temp.default_reason")
                    .replace("{reason}", reason)
                    .replace("{duration}", String.valueOf(durationMillis / 1000) + " seconds")
                    .sendTo(player);

            translator.getMessage("punishments.mute.temp.success")
                    .replace("{player}", player.getUsername())
                    .replace("{reason}", reason)
                    .replace("{duration}", String.valueOf(durationMillis / 1000) + " seconds")
                    .sendTo(staff);
        } else {
            translator.getMessage("punishments.mute.perm.default_reason")
                    .replace("{reason}", reason)
                    .sendTo(player);

            translator.getMessage("punishments.mute.perm.success")
                    .replace("{player}", player.getUsername())
                    .replace("{reason}", reason)
                    .sendTo(staff);
        }

        return punishment;
    }

    @Override
    public Punishment mutePlayer(FancyPlayer player, FancyPlayer staff, String reason) {
        return mutePlayer(player, staff, reason, -1);
    }

    @Override
    public Punishment kickPlayer(FancyPlayer player, FancyPlayer staff, String reason) {
        Punishment punishment = new PunishmentImpl(
                UUID.randomUUID(),
                player.getUUID(),
                PunishmentType.KICK,
                reason,
                System.currentTimeMillis(),
                staff.getUUID(),
                -1
        );

        if (!new PlayerPunishedEvent(player, punishment).fire()) {
            return null;
        }

        storage.createPunishment(punishment);

        // TODO: Kick the player from the server with a message
        String kickMessage = translator.getMessage("punishments.kick.default_reason")
                .replace("{reason}", reason)
                .getParsedMessage();

        translator.getMessage("punishments.kick.success")
                .replace("{player}", player.getUsername())
                .replace("{reason}", reason)
                .sendTo(staff);

        return punishment;
    }

    @Override
    public Punishment banPlayer(FancyPlayer player, FancyPlayer staff, String reason, long durationMillis) {
        long expiresAt = durationMillis <= 0 ? -1 : System.currentTimeMillis() + durationMillis;

        Punishment punishment = new PunishmentImpl(
                UUID.randomUUID(),
                player.getUUID(),
                PunishmentType.BAN,
                reason,
                System.currentTimeMillis(),
                staff.getUUID(),
                expiresAt
        );

        if (!new PlayerPunishedEvent(player, punishment).fire()) {
            return null;
        }

        storage.createPunishment(punishment);

        // TODO: Kick the player from the server with a ban message
        String kickMessage;
        if (durationMillis > 0) {
            kickMessage = translator.getMessage("punishments.ban.temp.default_reason")
                    .replace("{reason}", reason)
                    .replace("{duration}", String.valueOf(durationMillis / 1000) + " seconds")
                    .getParsedMessage();

            translator.getMessage("punishments.ban.temp.success")
                    .replace("{player}", player.getUsername())
                    .replace("{reason}", reason)
                    .replace("{duration}", String.valueOf(durationMillis / 1000) + " seconds")
                    .sendTo(staff);
        } else {
            kickMessage = translator.getMessage("punishments.ban.perm.default_reason")
                    .replace("{reason}", reason)
                    .getParsedMessage();

            translator.getMessage("punishments.ban.perm.success")
                    .replace("{player}", player.getUsername())
                    .replace("{reason}", reason)
                    .sendTo(staff);
        }

        return punishment;
    }

    @Override
    public Punishment banPlayer(FancyPlayer player, FancyPlayer staff, String reason) {
        return banPlayer(player, staff, reason, -1);
    }

    @Override
    public List<Punishment> getPunishmentsForPlayer(FancyPlayer player) {
        return storage.getPunishmentsForPlayer(player.getUUID());
    }

    @Override
    public void reportPlayer(FancyPlayer reported, FancyPlayer staff, String reason) {
        PlayerReportImpl report = new PlayerReportImpl(
                UUID.randomUUID(),
                reported,
                staff,
                reason
        );

        if (!new PlayerReportedEvent(reported, report).fire()) {
            return;
        }

        storage.createReport(report);

        translator.getMessage("punishments.report.successfully_reported")
                .replace("{reported}", reported.getUsername())
                .replace("{reason}", reason)
                .sendTo(staff);
    }
}
