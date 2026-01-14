package com.fancyinnovations.fancycore.kits.storage;

import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitStorage {

    private static final String DATA_DIR_PATH = "mods/FancyCore/data/kits";
    private final JDB db;

    public KitStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    public void saveKit(String kitName, Map<String, Object> kitData) {
        try {
            db.set(kitName, kitData);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to save kit",
                    ThrowableProperty.of(e)
            );
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getKit(String kitName) {
        try {
            return db.get(kitName, Map.class);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load kit",
                    ThrowableProperty.of(e)
            );
        }
        return null;
    }

    public void deleteKit(String kitName) {
        db.delete(kitName);
    }

    public boolean kitExists(String kitName) {
        Map<String, Object> kit = getKit(kitName);
        return kit != null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllKitNames() {
        try {
            List<Map> allKits = db.getAll("", Map.class);
            return allKits.stream()
                    .map(kit -> (String) kit.get("name"))
                    .filter(name -> name != null)
                    .toList();
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load kit names",
                    ThrowableProperty.of(e)
            );
        }
        return List.of();
    }
}
