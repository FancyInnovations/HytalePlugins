package com.fancyinnovations.fancycore.api;

import com.fancyinnovations.fancycore.api.events.service.EventService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerStorage;
import com.fancyinnovations.fancycore.api.punishments.PunishmentService;
import com.fancyinnovations.fancycore.api.punishments.PunishmentStorage;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.ScheduledExecutorService;

public interface FancyCore {

    static FancyCore get() {
        return InstanceHolder.getInstance();
    }

    ExtendedFancyLogger getFancyLogger();

    FancyCoreConfig getConfig();

    ScheduledExecutorService getThreadPool();

    EventService getEventService();

    FancyPlayerStorage getPlayerStorage();
    FancyPlayerService getPlayerService();

    PunishmentStorage getPunishmentStorage();
    PunishmentService getPunishmentService();


    @ApiStatus.Internal
    class InstanceHolder {
        private static FancyCore instance;

        @ApiStatus.Internal
        public static FancyCore getInstance() {
            return instance;
        }

        @ApiStatus.Internal
        public static void setInstance(FancyCore fancyCore) {
            if (instance != null) {
                throw new IllegalStateException("FancyCore instance is already set!");
            }

            instance = fancyCore;
        }
    }
}
