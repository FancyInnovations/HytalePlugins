package com.fancyinnovations.fancycore.inventory.storage.json;

import com.fancyinnovations.fancycore.api.inventory.Backpack;
import com.fancyinnovations.fancycore.api.inventory.BackpacksStorage;
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
import java.util.UUID;

public class BackpacksJsonStorage implements BackpacksStorage {

    private static final String BACKPACKS_DATA_DIR_PATH = "mods/FancyCore/data/backpacks";
    private final JDB db;

    public BackpacksJsonStorage() {
        this.db = new JDB(BACKPACKS_DATA_DIR_PATH);
    }

    @Override
    public Backpack getBackpack(UUID ownerUUID, String name) {
        try {
            String key = ownerUUID.toString() + "/" + name + "/backpack";
            return db.get(key, Backpack.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ItemStack> getBackpackItems(UUID ownerUUID, String name) {
        File itemsFile = new File(BACKPACKS_DATA_DIR_PATH + "/" + ownerUUID.toString() + "/" + name + "/items.json");
        if (!itemsFile.exists()) {
            return List.of();
        }

        try {
            String data = Files.readString(itemsFile.toPath());
            return ItemStackJsonStorageHelper.fromJson(data);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().warn(
                    "Failed to load Backpack items",
                    StringProperty.of("owner", ownerUUID.toString()),
                    StringProperty.of("backpack", name),
                    ThrowableProperty.of(e)
            );
            return List.of();
        }
    }

    @Override
    public List<Backpack> getBackpacks(UUID ownerUUID) {
        File ownerDir = new File(BACKPACKS_DATA_DIR_PATH + "/" + ownerUUID.toString());
        if (!ownerDir.exists() || !ownerDir.isDirectory()) {
            return List.of();
        }

        File[] backpackDirs = ownerDir.listFiles(File::isDirectory);
        if (backpackDirs == null) {
            return List.of();
        }

        List<Backpack> backpacks = new ArrayList<>();

        for (File backpackDir : backpackDirs) {
            try {
                String key = ownerUUID.toString() + "/" + backpackDir.getName() + "/backpack";
                Backpack backpack = db.get(key, Backpack.class);
                if (backpack != null) {
                    backpacks.add(backpack);
                }
            } catch (IOException e) {
                FancyCorePlugin.get().getFancyLogger().warn(
                        "Failed to load Backpack",
                        StringProperty.of("owner", ownerUUID.toString()),
                        StringProperty.of("backpack", backpackDir.getName()),
                        ThrowableProperty.of(e)
                );
            }
        }

        return backpacks;
    }

    @Override
    public void storeBackpack(Backpack backpack) {
        try {
            String key = backpack.ownerUUID().toString() + "/" + backpack.name() + "/backpack";
            db.set(key, backpack);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Backpack",
                    StringProperty.of("owner", backpack.ownerUUID().toString()),
                    StringProperty.of("backpack", backpack.name()),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public void storeBackpackItems(UUID ownerUUID, String name, List<ItemStack> items) {
        try {
            String itemsJson = ItemStackJsonStorageHelper.toJson(items);
            File itemsFile = new File(BACKPACKS_DATA_DIR_PATH + "/" + ownerUUID.toString() + "/" + name + "/items.json");
            itemsFile.getParentFile().mkdirs();
            Files.writeString(itemsFile.toPath(), itemsJson);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Backpack items",
                    StringProperty.of("owner", ownerUUID.toString()),
                    StringProperty.of("backpack", name),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public void deleteBackpack(UUID ownerUUID, String name) {
        try {
            String key = ownerUUID.toString() + "/" + name;
            db.delete(key);

            File itemsFile = new File(BACKPACKS_DATA_DIR_PATH + "/" + ownerUUID.toString() + "/" + name + "/items.json");
            if (itemsFile.exists()) {
                itemsFile.delete();
            }

            File backpackDir = new File(BACKPACKS_DATA_DIR_PATH + "/" + ownerUUID.toString() + "/" + name);
            if (backpackDir.exists() && !backpackDir.delete()) {
                FancyCorePlugin.get().getFancyLogger().warn(
                        "Failed to delete backpack directory",
                        StringProperty.of("owner", ownerUUID.toString()),
                        StringProperty.of("backpack", name)
                );
            }
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to delete Backpack",
                    StringProperty.of("owner", ownerUUID.toString()),
                    StringProperty.of("backpack", name),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public int countBackpacks() {
        File dir = new File(BACKPACKS_DATA_DIR_PATH);
        File[] ownerDirs = dir.listFiles(File::isDirectory);
        if (ownerDirs == null) {
            return 0;
        }

        int count = 0;

        for (File ownerDir : ownerDirs) {
            File[] backpackDirs = ownerDir.listFiles(File::isDirectory);
            if (backpackDirs != null) {
                count += backpackDirs.length;
            }
        }

        return count;
    }

}
