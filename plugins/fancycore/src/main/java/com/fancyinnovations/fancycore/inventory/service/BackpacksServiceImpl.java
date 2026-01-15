package com.fancyinnovations.fancycore.inventory.service;

import com.fancyinnovations.fancycore.api.inventory.Backpack;
import com.fancyinnovations.fancycore.api.inventory.BackpacksService;
import com.fancyinnovations.fancycore.api.inventory.BackpacksStorage;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class BackpacksServiceImpl implements BackpacksService {

    private final BackpacksStorage storage;

    public BackpacksServiceImpl(BackpacksStorage storage) {
        this.storage = storage;
    }

    @Override
    public Backpack getBackpack(UUID ownerUUID, String name) {
        return storage.getBackpack(ownerUUID, name);
    }

    @Override
    public List<ItemStack> getBackpackItems(UUID ownerUUID, String name) {
        return storage.getBackpackItems(ownerUUID, name);
    }

    @Override
    public List<Backpack> getBackpacks(UUID ownerUUID) {
        return storage.getBackpacks(ownerUUID);
    }

    @Override
    public void createBackpack(UUID ownerUUID, String name, int size) {
        Backpack backpack = new Backpack(ownerUUID, name, size);
        storage.storeBackpack(backpack);
        // Initialize with empty items
        storage.storeBackpackItems(ownerUUID, name, List.of());
    }

    @Override
    public void deleteBackpack(UUID ownerUUID, String name) {
        storage.deleteBackpack(ownerUUID, name);
    }

    @Override
    public void setBackpackItems(UUID ownerUUID, String name, List<ItemStack> items) {
        storage.storeBackpackItems(ownerUUID, name, items);
    }

}
