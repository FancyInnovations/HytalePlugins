package com.fancyinnovations.fancycore.moderation.storage.json;

import com.fancyinnovations.fancycore.api.moderation.PlayerReport;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.moderation.PunishmentStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.moderation.PunishmentImpl;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishmentJsonStorage implements PunishmentStorage {

    private static final String PUNISHMENTS_DATA_DIR_PATH = com.fancyinnovations.fancycore.config.FancyCorePaths.PUNISHMENTS_DATA_DIR;
    private static final String REPORTS_DATA_DIR_PATH = com.fancyinnovations.fancycore.config.FancyCorePaths.REPORTS_DATA_DIR;
    private final JDB punishmentDB;
    private final JDB reportDB;

    public PunishmentJsonStorage() {
        this.punishmentDB = new JDB(PUNISHMENTS_DATA_DIR_PATH);
        this.reportDB = new JDB(REPORTS_DATA_DIR_PATH);
    }

    @Override
    public void createPunishment(Punishment punishment) {
        try {
            punishmentDB.set(punishment.player().toString() + "/" + punishment.id().toString(), punishment);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Punishment",
                    StringProperty.of("id", punishment.id().toString()),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public List<Punishment> getPunishmentsForPlayer(UUID player) {
        try {
            List<PunishmentImpl> all = punishmentDB.getAll(player.toString(), PunishmentImpl.class);
            return new ArrayList<>(all);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load Punishments for player",
                    StringProperty.of("player", player.toString()),
                    ThrowableProperty.of(e)
            );
        }

        return List.of();
    }

    @Override
    public Punishment getPunishmentById(String id) {
        File dir = new File(PUNISHMENTS_DATA_DIR_PATH);
        File[] playerDirs = dir.listFiles(File::isDirectory);
        if (playerDirs == null) {
            return null;
        }

        for (File playerDir : playerDirs) {
            File punishmentFile = new File(playerDir, id + ".json");
            if (punishmentFile.exists()) {
                try {
                    return punishmentDB.get(playerDir.getName() + "/" + id, PunishmentImpl.class);
                } catch (IOException e) {
                    FancyCorePlugin.get().getFancyLogger().error(
                            "Failed to load Punishment by ID",
                            StringProperty.of("id", id),
                            ThrowableProperty.of(e)
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<Punishment> getAllPunishments() {
        File dir = new File(PUNISHMENTS_DATA_DIR_PATH);
        File[] playerDirs = dir.listFiles(File::isDirectory);
        if (playerDirs == null) {
            return List.of();
        }

        List<Punishment> all = new ArrayList<>();
        for (File playerDir : playerDirs) {
            all.addAll(getPunishmentsForPlayer(UUID.fromString(playerDir.getName())));
        }

        return all;
    }

    @Override
    public void createReport(PlayerReport report) {
        try {
            reportDB.set(report.id().toString(), JsonReport.from(report));
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store PlayerReport",
                    StringProperty.of("reportedPlayer", report.reportedPlayer().toString()),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public List<PlayerReport> getAllReports() {
        try {
            List<JsonReport> all = reportDB.getAll("", JsonReport.class);
            List<PlayerReport> reports = new ArrayList<>();
            for (JsonReport jsonReport : all) {
                reports.add(jsonReport.toPlayerReport());
            }
            return reports;
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load PlayerReports",
                    ThrowableProperty.of(e)
            );
        }

        return List.of();
    }

    @Override
    public PlayerReport getReportById(String id) {
        try {
            JsonReport jsonReport = reportDB.get(id, JsonReport.class);
            if (jsonReport != null) {
                return jsonReport.toPlayerReport();
            }
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load PlayerReport by ID",
                    StringProperty.of("id", id),
                    ThrowableProperty.of(e)
            );
        }

        return null;
    }

    @Override
    public int countTotalPunishments() {
        File dir = new File(PUNISHMENTS_DATA_DIR_PATH);
        File[] playerDirs = dir.listFiles(File::isDirectory);
        if (playerDirs == null) {
            return 0;
        }

        int count = 0;
        for (File playerDir : playerDirs) {
            File[] punishmentFiles = playerDir.listFiles((d, name) -> name.endsWith(".json"));
            if (punishmentFiles != null) {
                count += punishmentFiles.length;
            }
        }

        return count;
    }

    @Override
    public int countTotalReports() {
        File dir = new File(REPORTS_DATA_DIR_PATH);
        File[] reportFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (reportFiles == null) {
            return 0;
        }

        return reportFiles.length;
    }
}
