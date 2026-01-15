package com.fancyinnovations.fancycore.api.inventory;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface BackpacksService {

    static BackpacksService get() {
        return FancyCore.get().getBackpacksService();
    }

    Backpack getBackpack(UUID ownerUUID, String name);

    List<ItemStack> getBackpackItems(UUID ownerUUID, String name);

    List<Backpack> getBackpacks(UUID ownerUUID);

    void createBackpack(UUID ownerUUID, String name, int size);

    void deleteBackpack(UUID ownerUUID, String name);

    void setBackpackItems(UUID ownerUUID, String name, List<ItemStack> items);

}
