package com.fancyinnovations.fancycore.api.moderation;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;

import java.util.List;

public interface PunishmentService {

    static PunishmentService get() {
        return FancyCore.get().getPunishmentService();
    }

    Punishment warnPlayer(FancyPlayer player, FancyPlayer staff, String reason);

    Punishment mutePlayer(FancyPlayer player, FancyPlayer staff, String reason, long durationMillis);

    Punishment mutePlayer(FancyPlayer player, FancyPlayer staff, String reason);

    Punishment kickPlayer(FancyPlayer player, FancyPlayer staff, String reason);

    Punishment banPlayer(FancyPlayer player, FancyPlayer staff, String reason, long durationMillis);

    Punishment banPlayer(FancyPlayer player, FancyPlayer staff, String reason);

    /**
     * @return false if the player was not banned
     */
    boolean unban(FancyPlayer player);

    /**
     * @return false if the player was not muted
     */
    boolean unmute(FancyPlayer player);

    Punishment getPunishmentById(String id);

    List<Punishment> getPunishmentsForPlayer(FancyPlayer player);

    void reportPlayer(FancyPlayer reported, FancyPlayer staff, String reason);

    void resolveReport(PlayerReport report);

    PlayerReport getReportById(String id);

    List<PlayerReport> getAllReports();

}
