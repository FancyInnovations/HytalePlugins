package com.fancyinnovations.fancycore.api.inventory;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.List;

public interface KitsService {

    static KitsService get() {
        return FancyCore.get().getKitsService();
    }

    Kit getKit(String name);

    List<ItemStack> getKitItems(Kit kit);

    void giveKitToPlayer(Kit kit, FancyPlayer player);

    List<Kit> getAllKits();

    void createKit(Kit kit, List<ItemStack> items);

    void deleteKit(Kit kit);

}
