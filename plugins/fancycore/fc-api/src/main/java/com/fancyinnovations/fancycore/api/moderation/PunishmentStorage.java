package com.fancyinnovations.fancycore.api.moderation;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.UUID;

@ApiStatus.Internal
public interface PunishmentStorage {

    @ApiStatus.Internal
    void createPunishment(Punishment punishment);

    @ApiStatus.Internal
    List<Punishment> getPunishmentsForPlayer(UUID player);

    @ApiStatus.Internal
    Punishment getPunishmentById(String id);

    @ApiStatus.Internal
    List<Punishment> getAllPunishments();

    @ApiStatus.Internal
    void createReport(PlayerReport report);

    @ApiStatus.Internal
    List<PlayerReport> getAllReports();

    @ApiStatus.Internal
    PlayerReport getReportById(String id);

    @ApiStatus.Internal
    int countTotalPunishments();

    @ApiStatus.Internal
    int countTotalReports();
}
