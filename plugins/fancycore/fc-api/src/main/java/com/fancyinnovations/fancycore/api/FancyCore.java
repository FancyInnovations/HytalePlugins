package com.fancyinnovations.fancycore.api;

import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerStorage;
import com.fancyinnovations.fancycore.api.punishments.PunishmentStorage;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;

import java.util.concurrent.ScheduledExecutorService;

public interface FancyCore {

    ExtendedFancyLogger getFancyLogger();

    FancyCoreConfig getConfig();

    ScheduledExecutorService getThreadPool();

    FancyPlayerStorage getPlayerStorage();
    FancyPlayerService getPlayerService();

    PunishmentStorage getPunishmentStorage();

}
