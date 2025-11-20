package com.fancyinnovations.fancycore.api.punishments;

import java.util.List;
import java.util.UUID;

public interface PunishmentStorage {

    void createPunishment(Punishment punishment);

    List<Punishment> getPunishmentsForPlayer(UUID player);

    List<Punishment> getAllPunishments();
}
