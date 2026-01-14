package com.fancyinnovations.fancycore.teleport.storage;

import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpawnLocationStorage {

    private static final String DATA_DIR_PATH = "mods/FancyCore/data";
    private static final String SPAWN_KEY = "spawn";
    private final JDB db;

    public SpawnLocationStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    public void setSpawnLocation(String world, double x, double y, double z, double yaw, double pitch) {
        try {
            Map<String, Object> spawnData = new HashMap<>();
            spawnData.put("world", world);
            spawnData.put("x", x);
            spawnData.put("y", y);
            spawnData.put("z", z);
            spawnData.put("yaw", yaw);
            spawnData.put("pitch", pitch);
            db.set(SPAWN_KEY, spawnData);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to save spawn location",
                    ThrowableProperty.of(e)
            );
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getSpawnLocation() {
        try {
            return db.get(SPAWN_KEY, Map.class);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load spawn location",
                    ThrowableProperty.of(e)
            );
        }
        return null;
    }
}
