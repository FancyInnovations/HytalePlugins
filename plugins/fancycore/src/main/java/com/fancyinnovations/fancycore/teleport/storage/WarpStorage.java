package com.fancyinnovations.fancycore.teleport.storage;

import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WarpStorage {

    private static final String DATA_DIR_PATH = "mods/FancyCore/data";
    private static final String WARPS_KEY = "warps";
    private final JDB db;

    public WarpStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    public void setWarp(String name, String world, double x, double y, double z, double yaw, double pitch) {
        try {
            // Get existing warps
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> warps = db.get(WARPS_KEY, Map.class);
            if (warps == null) {
                warps = new HashMap<>();
            }

            // Create warp data
            Map<String, Object> warpData = new HashMap<>();
            warpData.put("world", world);
            warpData.put("x", x);
            warpData.put("y", y);
            warpData.put("z", z);
            warpData.put("yaw", yaw);
            warpData.put("pitch", pitch);

            // Save warp
            warps.put(name, warpData);
            db.set(WARPS_KEY, warps);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to save warp",
                    ThrowableProperty.of(e)
            );
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> getAllWarps() {
        try {
            Map<String, Map<String, Object>> warps = db.get(WARPS_KEY, Map.class);
            return warps != null ? warps : new HashMap<>();
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load warps",
                    ThrowableProperty.of(e)
            );
        }
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getWarp(String name) {
        try {
            Map<String, Map<String, Object>> warps = db.get(WARPS_KEY, Map.class);
            if (warps == null) {
                return null;
            }
            return warps.get(name);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load warp",
                    ThrowableProperty.of(e)
            );
        }
        return null;
    }

    public void deleteWarp(String name) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> warps = db.get(WARPS_KEY, Map.class);
            if (warps == null) {
                return;
            }
            warps.remove(name);
            db.set(WARPS_KEY, warps);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to delete warp",
                    ThrowableProperty.of(e)
            );
        }
    }

    public boolean warpExists(String name) {
        Map<String, Object> warp = getWarp(name);
        return warp != null;
    }
}
