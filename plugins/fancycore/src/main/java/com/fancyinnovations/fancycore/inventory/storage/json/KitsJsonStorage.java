package com.fancyinnovations.fancycore.inventory.storage.json;

import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class KitsJsonStorage implements KitsStorage {

    private static final String PUNISHMENTS_DATA_DIR_PATH = "mods/FancyCore/data/kits";
    private final JDB db;

    public KitsJsonStorage() {
        this.db = new JDB(PUNISHMENTS_DATA_DIR_PATH);
    }

    @Override
    public Kit getKit(String name) {
        try {
            return db.get(name + "/kit", Kit.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ItemStack> getKitItems(String kitName) {
        File itemsFile = new File(PUNISHMENTS_DATA_DIR_PATH + "/" + kitName + "/items.json");
        if (!itemsFile.exists()) {
            return List.of();
        }

        try {
            String data = Files.readString(itemsFile.toPath());
            return ItemStackJsonStorageHelper.fromJson(data);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().warn(
                    "Failed to load Kit items",
                    StringProperty.of("kit", kitName),
                    ThrowableProperty.of(e)
            );
            return List.of();
        }
    }

    @Override
    public List<Kit> getKits() {
        File dir = new File(PUNISHMENTS_DATA_DIR_PATH);
        File[] kitDirs = dir.listFiles(File::isDirectory);
        if (kitDirs == null) {
            return List.of();
        }

        List<Kit> kits = new ArrayList<>();

        for (File kitDir : kitDirs) {
            try {
                Kit kit = db.get(kitDir.getName() + "/kit", Kit.class);
                if (kit != null) {
                    kits.add(kit);
                }
            } catch (IOException e) {
                FancyCorePlugin.get().getFancyLogger().warn(
                        "Failed to load Kit",
                        StringProperty.of("kit", kitDir.getName()),
                        ThrowableProperty.of(e)
                );
            }
        }

        return kits;
    }

    @Override
    public void storeKit(Kit kit, List<ItemStack> items) {
        try {
            db.set(kit.name() + "/kit", kit);

            String itemsJson = ItemStackJsonStorageHelper.toJson(items);
            File itemsFile = new File(PUNISHMENTS_DATA_DIR_PATH + "/" + kit.name() + "/items.json");
            itemsFile.getParentFile().mkdirs();
            Files.writeString(itemsFile.toPath(), itemsJson);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Kit",
                    StringProperty.of("kit", kit.name()),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public void deleteKit(Kit kit) {
        db.delete(kit.name());

        File itemsFile = new File(PUNISHMENTS_DATA_DIR_PATH + "/" + kit.name() + "/items.json");
        if (itemsFile.exists()) {
            itemsFile.delete();
        }
    }

    @Override
    public int countKits() {
        File dir = new File(PUNISHMENTS_DATA_DIR_PATH);
        File[] kitDirs = dir.listFiles(File::isDirectory);
        if (kitDirs == null) {
            return 0;
        }
        return kitDirs.length;
    }
}
