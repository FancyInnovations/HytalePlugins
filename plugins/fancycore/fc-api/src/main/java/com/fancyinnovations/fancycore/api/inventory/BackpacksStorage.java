package com.fancyinnovations.fancycore.api.inventory;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.UUID;

@ApiStatus.Internal
public interface BackpacksStorage {

    @ApiStatus.Internal
    Backpack getBackpack(UUID ownerUUID, String name);

    @ApiStatus.Internal
    List<ItemStack> getBackpackItems(UUID ownerUUID, String name);

    @ApiStatus.Internal
    List<Backpack> getBackpacks(UUID ownerUUID);

    @ApiStatus.Internal
    void storeBackpack(Backpack backpack);

    @ApiStatus.Internal
    void storeBackpackItems(UUID ownerUUID, String name, List<ItemStack> items);

    @ApiStatus.Internal
    void deleteBackpack(UUID ownerUUID, String name);

    @ApiStatus.Internal
    int countBackpacks();

}
