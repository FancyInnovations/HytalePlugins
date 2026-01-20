package com.fancyinnovations.fancycore.player.service;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CleanUpPlayerCacheRunnable implements Runnable {

    private final FancyPlayerServiceImpl service;
    private final FancyCorePlugin plugin;
    private ScheduledFuture<?> schedule;

    public CleanUpPlayerCacheRunnable() {
        FancyCorePlugin pluginInstance = FancyCorePlugin.get();
        if (pluginInstance == null) {
            throw new IllegalStateException("FancyCorePlugin instance is not available");
        }
        this.plugin = pluginInstance;
        this.service = (FancyPlayerServiceImpl) plugin.getPlayerService();
    }

    @Override
    public void run() {
        for (FancyPlayer fp : service.getAllCached()) {
            if (!fp.isOnline()) {
                service.removePlayerFromCache(fp.getData().getUUID());
            }
        }
    }

    public ScheduledFuture<?> schedule() {
        if (this.schedule != null && !this.schedule.isCancelled()) {
            throw new IllegalStateException("CleanUpPlayerCacheRunnable is already scheduled");
        }

        this.schedule = plugin.getThreadPool().scheduleWithFixedDelay(
                this,
                10,
                30,
                TimeUnit.MINUTES
        );

        return this.schedule;
    }

    public ScheduledFuture<?> getSchedule() {
        return schedule;
    }
}
