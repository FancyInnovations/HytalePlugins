package com.fancyinnovations.fancycore.player.storage;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import de.oliver.fancyanalytics.logger.properties.NumberProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SavePlayersRunnable implements Runnable{

    private final FancyPlayerServiceImpl service;
    private final FancyPlayerStorage storage;
    private ScheduledFuture<?> schedule;

    public SavePlayersRunnable() {
        this.service = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();
        this.storage = FancyCorePlugin.get().getPlayerStorage();
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();

            List<FancyPlayer> all = service.getAllCached();
            for (FancyPlayer fp : all) {
                if (fp.getData().isDirty()) {
                    storage.savePlayer(fp.getData());
                    fp.getData().setDirty(false);
                }
            }

            long duration = System.currentTimeMillis() - start;
            FancyCorePlugin.get().getFancyLogger().info(
                    "Saved player data",
                    NumberProperty.of("count", all.size()),
                    NumberProperty.of("duration_ms", duration)
            );
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().warn("Failed to save player data", ThrowableProperty.of(e));
        }
    }

    public ScheduledFuture<?> schedule() {
        if (this.schedule != null && !this.schedule.isCancelled()) {
            throw new IllegalStateException("SavePlayersRunnable is already scheduled");
        }

        this.schedule = FancyCorePlugin.get().getThreadPool().scheduleWithFixedDelay(
                this,
                5L * 60L,
                60L * 60L,
                TimeUnit.SECONDS
        );

        return this.schedule;
    }

    public ScheduledFuture<?> getSchedule() {
        return schedule;
    }
}
