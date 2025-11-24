package com.fancyinnovations.fancycore.player.service;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CleanUpPlayerCacheRunnable implements Runnable{

    private final FancyPlayerServiceImpl service;
    private ScheduledFuture<?> schedule;

    public CleanUpPlayerCacheRunnable() {
        this.service = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();
    }

    @Override
    public void run() {
        for (FancyPlayer fp : service.getAllCached()) {
            //TODO: uncomment once FancyPlayer#isOnline is implemented
            if (!fp.isOnline()) {
                service.removePlayerFromCache(fp.getData().getUUID());
                service.removeOnlinePlayer(fp);
            }
        }
    }

    public ScheduledFuture<?> schedule() {
        if (this.schedule != null && !this.schedule.isCancelled()) {
            throw new IllegalStateException("CleanUpPlayerCacheRunnable is already scheduled");
        }

        this.schedule = FancyCorePlugin.get().getThreadPool().scheduleWithFixedDelay(
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
