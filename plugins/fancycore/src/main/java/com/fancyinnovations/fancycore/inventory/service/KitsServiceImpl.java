package com.fancyinnovations.fancycore.inventory.service;

import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.inventory.KitsStorage;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.inventory.storage.json.KitsJsonStorage;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.universe.Universe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KitsServiceImpl implements KitsService {

    private final KitsStorage storage;
    private final Map<String, Kit> kitsCache;
    private final Map<String, List<ItemStack>> kitItemsCache;

    public KitsServiceImpl(KitsStorage storage) {
        this.storage = storage;
        this.kitsCache = new ConcurrentHashMap<>();
        this.kitItemsCache = new ConcurrentHashMap<>();
        load();
    }

    private void load() {
        for (Kit kit : storage.getKits()) {
            kitsCache.put(kit.name(), kit);
            // Load items for backward compatibility
            kitItemsCache.put(kit.name(), storage.getKitItems(kit.name()));
        }
    }

    @Override
    public Kit getKit(String name) {
        return kitsCache.get(name);
    }

    @Override
    public List<ItemStack> getKitItems(Kit kit) {
        return kitItemsCache.getOrDefault(kit.name(), List.of());
    }

    @Override
    public void giveKitToPlayer(Kit kit, FancyPlayer fp) {
        if (!fp.isOnline()) {
            return;
        }

        var playerRef = fp.getPlayer();
        if (playerRef == null || playerRef.getWorldUuid() == null) {
            return;
        }

        var world = Universe.get().getWorld(playerRef.getWorldUuid());
        if (world == null) {
            return;
        }

        world.execute(() -> {
            var ref = playerRef.getReference();
            if (ref == null || !ref.isValid()) {
                return;
            }
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            if (player == null) {
                fp.sendMessage("You are not an player");
                return;
            }

            ItemContainer hotbar = player.getInventory().getHotbar();
            ItemContainer storageContainer = player.getInventory().getStorage();
            ItemContainer armorContainer = player.getInventory().getArmor();

            // Load and place items from all containers
            if (storage instanceof KitsJsonStorage) {
                KitsJsonStorage jsonStorage = (KitsJsonStorage) storage;
                
                // Place items in each container
                placeContainerItems(jsonStorage, kit.name(), "hotbar", hotbar, fp);
                placeContainerItems(jsonStorage, kit.name(), "storage", storageContainer, fp);
                placeContainerItems(jsonStorage, kit.name(), "armor", armorContainer, fp);
            } else {
                // Fallback: try to add all items to hotbar first, then storage
                List<ItemStack> items = KitsService.get().getKitItems(kit);
                for (ItemStack item : items) {
                    if (!tryToAddItemToContainer(item, hotbar)) {
                        if (!tryToAddItemToContainer(item, storageContainer)) {
                            fp.sendMessage("Not enough space in inventory for: " + item.toString());
                        }
                    }
                }
            }
        });

    }

    @Override
    public List<Kit> getAllKits() {
        return List.copyOf(kitsCache.values());
    }

    @Override
    public void createKit(Kit kit, List<ItemStack> items) {
        storage.storeKit(kit, items);
        kitsCache.put(kit.name(), kit);
        kitItemsCache.put(kit.name(), items);
    }

    public void createKitWithContainers(Kit kit, List<ItemStack> hotbarItems, List<ItemStack> storageItems, List<ItemStack> armorItems) {
        if (storage instanceof KitsJsonStorage) {
            KitsJsonStorage jsonStorage = (KitsJsonStorage) storage;
            jsonStorage.storeKitContainer(kit, "hotbar", hotbarItems);
            jsonStorage.storeKitContainer(kit, "storage", storageItems);
            jsonStorage.storeKitContainer(kit, "armor", armorItems);
        } else {
            // Fallback: combine items
            List<ItemStack> allItems = new java.util.ArrayList<>(hotbarItems);
            allItems.addAll(storageItems);
            allItems.addAll(armorItems);
            storage.storeKit(kit, allItems);
        }
        kitsCache.put(kit.name(), kit);
        // Cache combined items
        List<ItemStack> allItems = new java.util.ArrayList<>(hotbarItems);
        allItems.addAll(storageItems);
        allItems.addAll(armorItems);
        kitItemsCache.put(kit.name(), allItems);
    }

    @Override
    public void deleteKit(Kit kit) {
        storage.deleteKit(kit);
        kitsCache.remove(kit.name());
        kitItemsCache.remove(kit.name());
    }

    private void placeContainerItems(KitsJsonStorage storage, String kitName, String containerName, ItemContainer container, FancyPlayer fp) {
        List<ItemStack> items = storage.getKitContainerItems(kitName, containerName);
        for (int i = 0; i < items.size() && i < container.getCapacity(); i++) {
            ItemStack item = items.get(i);
            if (item != null) {
                if (container.getItemStack((short) i) == null) {
                    container.setItemStackForSlot((short) i, item);
                } else {
                    // Slot is occupied, try to add it elsewhere
                    if (!tryToAddItemToContainer(item, container)) {
                        fp.sendMessage("Not enough space in " + containerName + " for: " + item.toString());
                    }
                }
            }
        }
    }

    private boolean tryToAddItemToContainer(ItemStack item, ItemContainer container) {
        if (!container.canAddItemStack(item)) {
            return false;
        }

        ItemStackTransaction itemStackTransaction = container.addItemStack(item);
        if (!itemStackTransaction.succeeded()) {
            return false;
        }

        return true;
    }
}
