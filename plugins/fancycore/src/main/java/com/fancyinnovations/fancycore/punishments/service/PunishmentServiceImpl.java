package com.fancyinnovations.fancycore.punishments.service;

import com.fancyinnovations.fancycore.api.events.punishments.PlayerPunishedEvent;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.punishments.Punishment;
import com.fancyinnovations.fancycore.api.punishments.PunishmentService;
import com.fancyinnovations.fancycore.api.punishments.PunishmentStorage;
import com.fancyinnovations.fancycore.api.punishments.PunishmentType;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.punishments.PunishmentImpl;

import java.util.List;
import java.util.UUID;

public class PunishmentServiceImpl implements PunishmentService {

    private final PunishmentStorage storage;

    public PunishmentServiceImpl() {
        this.storage = FancyCorePlugin.get().getPunishmentStorage();
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

        // TODO: Notify player about the warning

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

        // TODO: Notify player about the mute

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
}
