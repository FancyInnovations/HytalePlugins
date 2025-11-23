package com.fancyinnovations.fancycore.api.punishments;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;

import java.util.List;

public interface PunishmentService {

    Punishment warnPlayer(FancyPlayer player, FancyPlayer staff, String reason);

    Punishment mutePlayer(FancyPlayer player, FancyPlayer staff, String reason, long durationMillis);
    Punishment mutePlayer(FancyPlayer player, FancyPlayer staff, String reason);

    Punishment kickPlayer(FancyPlayer player, FancyPlayer staff, String reason);

    Punishment banPlayer(FancyPlayer player, FancyPlayer staff, String reason, long durationMillis);
    Punishment banPlayer(FancyPlayer player, FancyPlayer staff, String reason);

    List<Punishment> getPunishmentsForPlayer(FancyPlayer player);

    void reportPlayer(FancyPlayer reported, FancyPlayer staff, String reason);

}
