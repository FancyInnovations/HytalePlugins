package com.fancyinnovations.fancycore.moderation.service;

import com.fancyinnovations.fancycore.api.events.player.PlayerPunishedEvent;
import com.fancyinnovations.fancycore.api.events.player.PlayerReportedEvent;
import com.fancyinnovations.fancycore.api.moderation.*;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.moderation.PlayerReportImpl;
import com.fancyinnovations.fancycore.moderation.PunishmentImpl;
import com.fancyinnovations.fancycore.translations.TranslationService;
import com.fancyinnovations.fancycore.utils.IDGen;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import com.hypixel.hytale.server.core.universe.Universe;

import java.util.List;

public class PunishmentServiceImpl implements PunishmentService {

    private final PunishmentStorage storage;
    private final TranslationService translator;

    public PunishmentServiceImpl() {
        this.storage = FancyCorePlugin.get().getPunishmentStorage();
        this.translator = FancyCorePlugin.get().getTranslationService();

        translator
                .addMessage("punishments.warning.default_reason", "You have been warned for: {reason}.")
                .addMessage("punishments.mute.perm.default_reason", "You have been muted for: {reason}.")
                .addMessage("punishments.mute.temp.default_reason", "You have been temporarily muted for: {reason}. Duration: {duration}.")
                .addMessage("punishments.kick.default_reason", "You have been kicked from the server for: {reason}.")
                .addMessage("punishments.ban.perm.default_reason", "You have been banned from the server.\nReason: {reason}.")
                .addMessage("punishments.ban.temp.default_reason", "You have been temporarily banned from the server for: {reason}. Duration: {duration}.")
        ;
    }

    @Override
    public Punishment warnPlayer(FancyPlayer player, FancyPlayer staff, String reason) {
        Punishment punishment = new PunishmentImpl(
                IDGen.generate(8),
                player.getData().getUUID(),
                PunishmentType.WARNING,
                reason,
                System.currentTimeMillis(),
                staff.getData().getUUID(),
                -1
        );

        if (!new PlayerPunishedEvent(player, punishment).fire()) {
            return null;
        }

        storage.createPunishment(punishment);

        translator.getMessage("punishments.warning.default_reason")
                .replace("reason", reason)
                .sendTo(player);

        return punishment;
    }

    @Override
    public Punishment mutePlayer(FancyPlayer player, FancyPlayer staff, String reason, long durationMillis) {
        long expiresAt = durationMillis <= 0 ? -1 : System.currentTimeMillis() + durationMillis;

        Punishment punishment = new PunishmentImpl(
                IDGen.generate(8),
                player.getData().getUUID(),
                PunishmentType.MUTE,
                reason,
                System.currentTimeMillis(),
                staff.getData().getUUID(),
                expiresAt
        );

        if (!new PlayerPunishedEvent(player, punishment).fire()) {
            return null;
        }

        storage.createPunishment(punishment);

        if (durationMillis > 0) {
            translator.getMessage("punishments.mute.temp.default_reason")
                    .replace("reason", reason)
                    .replace("duration", TimeUtils.formatTime(durationMillis))
                    .sendTo(player);
        } else {
            translator.getMessage("punishments.mute.perm.default_reason")
                    .replace("reason", reason)
                    .sendTo(player);
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
                IDGen.generate(8),
                player.getData().getUUID(),
                PunishmentType.KICK,
                reason,
                System.currentTimeMillis(),
                staff.getData().getUUID(),
                -1
        );

        if (!new PlayerPunishedEvent(player, punishment).fire()) {
            return null;
        }

        storage.createPunishment(punishment);

        String kickMessage = translator.getMessage("punishments.kick.default_reason")
                .replace("reason", reason)
                .getParsedMessage();

        Universe.get().getWorld(player.getPlayer().getWorldUuid()).execute(() -> {
            player.getPlayer().getPacketHandler().disconnect(kickMessage);
        });

        return punishment;
    }

    @Override
    public Punishment banPlayer(FancyPlayer player, FancyPlayer staff, String reason, long durationMillis) {
        long expiresAt = durationMillis <= 0 ? -1 : System.currentTimeMillis() + durationMillis;

        Punishment punishment = new PunishmentImpl(
                IDGen.generate(8),
                player.getData().getUUID(),
                PunishmentType.BAN,
                reason,
                System.currentTimeMillis(),
                staff.getData().getUUID(),
                expiresAt
        );

        if (!new PlayerPunishedEvent(player, punishment).fire()) {
            return null;
        }

        storage.createPunishment(punishment);

        String kickMessage;
        if (durationMillis > 0) {
            kickMessage = translator.getMessage("punishments.ban.temp.default_reason")
                    .replace("reason", reason)
                    .replace("duration", TimeUtils.formatTime(durationMillis))
                    .getParsedMessage();
        } else {
            kickMessage = translator.getMessage("punishments.ban.perm.default_reason")
                    .replace("reason", reason)
                    .getParsedMessage();
        }

        player.getPlayer().getPacketHandler().disconnect(kickMessage);

        return punishment;
    }

    @Override
    public Punishment banPlayer(FancyPlayer player, FancyPlayer staff, String reason) {
        return banPlayer(player, staff, reason, -1);
    }

    @Override
    public boolean unban(FancyPlayer player) {
        List<Punishment> punishments = getPunishmentsForPlayer(player);
        for (Punishment punishment : punishments) {
            if (punishment.type() != PunishmentType.BAN || !punishment.isActive()) {
                continue;
            }

            Punishment updatedPunishment = new PunishmentImpl(
                    punishment.id(),
                    punishment.player(),
                    punishment.type(),
                    punishment.reason(),
                    punishment.issuedAt(),
                    punishment.issuedBy(),
                    System.currentTimeMillis() - 1000
            );
            storage.createPunishment(updatedPunishment);

            return true;
        }

        return false;
    }

    @Override
    public boolean unmute(FancyPlayer player) {
        List<Punishment> punishments = getPunishmentsForPlayer(player);
        for (Punishment punishment : punishments) {
            if (punishment.type() != PunishmentType.MUTE || !punishment.isActive()) {
                continue;
            }

            Punishment updatedPunishment = new PunishmentImpl(
                    punishment.id(),
                    punishment.player(),
                    punishment.type(),
                    punishment.reason(),
                    punishment.issuedAt(),
                    punishment.issuedBy(),
                    System.currentTimeMillis() - 1000
            );
            storage.createPunishment(updatedPunishment);

            return true;
        }

        return false;
    }

    @Override
    public Punishment getPunishmentById(String id) {
        return storage.getPunishmentById(id);
    }

    @Override
    public List<Punishment> getPunishmentsForPlayer(FancyPlayer player) {
        return storage.getPunishmentsForPlayer(player.getData().getUUID());
    }

    @Override
    public void reportPlayer(FancyPlayer reported, FancyPlayer staff, String reason) {
        PlayerReportImpl report = new PlayerReportImpl(
                IDGen.generate(8),
                reported,
                staff,
                reason
        );

        if (!new PlayerReportedEvent(reported, report).fire()) {
            return;
        }

        storage.createReport(report);
    }

    @Override
    public void resolveReport(PlayerReport report) {
        ((PlayerReportImpl) report).resolve();
        storage.createReport(report);
    }

    @Override
    public PlayerReport getReportById(String id) {
        return storage.getReportById(id);
    }

    @Override
    public List<PlayerReport> getAllReports() {
        return storage.getAllReports();
    }
}
