package com.fancyinnovations.fancycore.teleport.storage.json;

import com.fancyinnovations.fancycore.api.teleport.Warp;
import com.fancyinnovations.fancycore.api.teleport.WarpStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.List;

public class WarpJsonStorage implements WarpStorage {

    private static final String DATA_DIR_PATH = com.fancyinnovations.fancycore.config.FancyCorePaths.WARPS_DATA_DIR;
    private final JDB db;

    public WarpJsonStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    @Override
    public Warp getWarp(String name) {
        try {
            return db.get(name.toLowerCase(), Warp.class);
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load Warp: " + name,
                    ThrowableProperty.of(e)
            );
        }

        return null;
    }

    @Override
    public List<Warp> getAllWarps() {
        try {
            return db.getAll("", Warp.class);
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load all Warps",
                    ThrowableProperty.of(e)
            );
        }

        return List.of();
    }

    @Override
    public void storeWarp(Warp warp) {
        try {
            db.set(warp.name().toLowerCase(), warp);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Warp: " + warp.name(),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public void deleteWarp(String name) {
        db.delete(name.toLowerCase());
    }
}
