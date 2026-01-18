package com.fancyinnovations.fancycore.api.inventory;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public interface KitsStorage {

    @ApiStatus.Internal
    Kit getKit(String name);

    @ApiStatus.Internal
    List<ItemStack> getKitItems(String kitName);

    @ApiStatus.Internal
    List<Kit> getKits();

    @ApiStatus.Internal
    void storeKit(Kit kit, List<ItemStack> items);

    @ApiStatus.Internal
    void deleteKit(Kit kit);

    @ApiStatus.Internal
    int countKits();

}
