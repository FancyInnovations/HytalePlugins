package com.fancyinnovations.fancycore.teleport.storage.json;

import com.fancyinnovations.fancycore.api.teleport.SpawnLocation;
import com.fancyinnovations.fancycore.api.teleport.SpawnStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;

public class SpawnJsonStorage implements SpawnStorage {

    private static final String DATA_DIR_PATH = "mods/FancyCore/data";
    private static final String SPAWN_KEY = "spawn";
    private final JDB db;

    public SpawnJsonStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }


    @Override
    public SpawnLocation loadSpawnLocation() {
        try {
            return db.get(SPAWN_KEY, SpawnLocation.class);
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load Spawn Location",
                    ThrowableProperty.of(e)
            );
        }

        return null;
    }

    @Override
    public void storeSpawnLocation(SpawnLocation location) {
        try {
            db.set(SPAWN_KEY, location);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Spawn Location",
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public void deleteSpawnLocation() {
        db.delete(SPAWN_KEY);
    }
}
