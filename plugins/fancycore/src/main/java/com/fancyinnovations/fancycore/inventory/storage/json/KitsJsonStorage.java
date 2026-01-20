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

    private static final String PUNISHMENTS_DATA_DIR_PATH = com.fancyinnovations.fancycore.config.FancyCorePaths.KITS_DATA_DIR;
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
        // Legacy method - combine all containers
        List<ItemStack> allItems = new ArrayList<>();
        allItems.addAll(getKitContainerItems(kitName, "hotbar"));
        allItems.addAll(getKitContainerItems(kitName, "storage"));
        allItems.addAll(getKitContainerItems(kitName, "armor"));
        return allItems;
    }

    public List<ItemStack> getKitContainerItems(String kitName, String containerName) {
        File containerFile = new File(PUNISHMENTS_DATA_DIR_PATH + "/" + kitName + "/" + containerName + ".json");
        if (!containerFile.exists()) {
            return List.of();
        }

        try {
            String data = Files.readString(containerFile.toPath());
            return ItemStackJsonStorageHelper.fromJson(data);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().warn(
                    "Failed to load Kit " + containerName + " items",
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
        // Legacy method - store all items in storage.json
        storeKitContainer(kit, "storage", items);
    }

    public void storeKitContainer(Kit kit, String containerName, List<ItemStack> items) {
        try {
            db.set(kit.name() + "/kit", kit);

            File kitDir = new File(PUNISHMENTS_DATA_DIR_PATH + "/" + kit.name());
            kitDir.mkdirs();

            String itemsJson = ItemStackJsonStorageHelper.toJson(items);
            File containerFile = new File(kitDir, containerName + ".json");
            Files.writeString(containerFile.toPath(), itemsJson);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Kit " + containerName,
                    StringProperty.of("kit", kit.name()),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public void deleteKit(Kit kit) {
        db.delete(kit.name());

        File kitDir = new File(PUNISHMENTS_DATA_DIR_PATH + "/" + kit.name());
        if (kitDir.exists()) {
            // Delete all container files
            for (String containerName : new String[]{"hotbar", "storage", "armor"}) {
                File containerFile = new File(kitDir, containerName + ".json");
                if (containerFile.exists()) {
                    containerFile.delete();
                }
            }
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
