package com.fancyinnovations.fancycore.punishments.storage.fake;

import com.fancyinnovations.fancycore.api.punishments.PlayerReport;
import com.fancyinnovations.fancycore.api.punishments.Punishment;
import com.fancyinnovations.fancycore.api.punishments.PunishmentStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishmentFakeStorage implements PunishmentStorage {

    private final List<Punishment> punishments;
    private final List<PlayerReport> reports;

    public PunishmentFakeStorage() {
        this.punishments = new ArrayList<>();
        this.reports = new ArrayList<>();
    }

    @Override
    public void createPunishment(Punishment punishment) {
        this.punishments.add(punishment);
    }

    @Override
    public List<Punishment> getPunishmentsForPlayer(UUID player) {
        return this.punishments.stream()
                .filter(punishment -> punishment.player().equals(player))
                .toList();
    }

    @Override
    public List<Punishment> getAllPunishments() {
        return new ArrayList<>(this.punishments);
    }

    @Override
    public void createReport(PlayerReport report) {
        this.reports.add(report);
    }

    @Override
    public List<PlayerReport> getAllReports() {
        return new ArrayList<>(this.reports);
    }
}
