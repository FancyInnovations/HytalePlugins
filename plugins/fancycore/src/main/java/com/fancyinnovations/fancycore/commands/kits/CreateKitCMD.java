package com.fancyinnovations.fancycore.commands.kits;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.kits.storage.KitStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.bson.BsonDocument;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CreateKitCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("", "Kit name", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);

    public CreateKitCMD() {
        super("createkit", "Creates a new kit with the specified name (note: it contains all items in your inventory)");
        requirePermission("fancycore.commands.createkit");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        String kitName = nameArg.get(ctx);
        if (kitName == null || kitName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Kit name cannot be empty."));
            return;
        }

        Ref<EntityStore> senderRef = ctx.senderAsPlayerRef();
        if (senderRef == null || !senderRef.isValid()) {
            ctx.sendMessage(Message.raw("You are not in a world."));
            return;
        }

        Store<EntityStore> senderStore = senderRef.getStore();
        World senderWorld = ((EntityStore) senderStore.getExternalData()).getWorld();

        // Execute on the world thread to access inventory
        senderWorld.execute(() -> {
            Player player = (Player) senderStore.getComponent(senderRef, Player.getComponentType());
            if (player == null) {
                ctx.sendMessage(Message.raw("Failed to get player component."));
                return;
            }

            Inventory inventory = player.getInventory();
            if (inventory == null) {
                ctx.sendMessage(Message.raw("Failed to access your inventory."));
                return;
            }

            KitStorage kitStorage = com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getKitStorage();
            
            // Check if kit already exists
            if (kitStorage.kitExists(kitName)) {
                ctx.sendMessage(Message.raw("A kit with the name \"" + kitName + "\" already exists."));
                return;
            }

            // Collect all items from inventory
            Map<String, Object> inventoryData = new HashMap<>();
            
            // Get all inventory sections
            ItemContainer storage = inventory.getStorage();
            ItemContainer hotbar = inventory.getHotbar();
            ItemContainer armor = inventory.getArmor();
            ItemContainer utility = inventory.getUtility();
            ItemContainer backpack = inventory.getBackpack();
            ItemContainer tools = inventory.getTools();

            // Save items from each container
            int storageCap = storage.getCapacity();
            int hotbarCap = hotbar.getCapacity();
            int armorCap = armor.getCapacity();
            int utilityCap = utility.getCapacity();
            int backpackCap = backpack.getCapacity();
            
            saveItemsFromContainer(storage, inventoryData, 0);
            saveItemsFromContainer(hotbar, inventoryData, storageCap);
            saveItemsFromContainer(armor, inventoryData, storageCap + hotbarCap);
            saveItemsFromContainer(utility, inventoryData, storageCap + hotbarCap + armorCap);
            saveItemsFromContainer(backpack, inventoryData, storageCap + hotbarCap + armorCap + utilityCap);
            saveItemsFromContainer(tools, inventoryData, storageCap + hotbarCap + armorCap + utilityCap + backpackCap);

            // Create kit data structure
            Map<String, Object> kitData = new HashMap<>();
            kitData.put("name", kitName);
            kitData.put("permission", "fancycore.commands.kit." + kitName.toLowerCase());
            kitData.put("description", "Kit: " + kitName);
            kitData.put("inventory", inventoryData);

            // Save kit
            kitStorage.saveKit(kitName, kitData);

            ctx.sendMessage(Message.raw("Kit \"" + kitName + "\" created successfully with " + inventoryData.size() + " items."));
        });
    }

    private void saveItemsFromContainer(ItemContainer container, Map<String, Object> inventoryData, int baseSlot) {
        if (container == null) {
            return;
        }
        
        for (short slot = 0; slot < container.getCapacity(); slot++) {
            ItemStack itemStack = container.getItemStack(slot);
            if (itemStack != null && !itemStack.isEmpty()) {
                int globalSlot = baseSlot + slot;
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("identifier", itemStack.getItemId());
                
                Map<String, Object> metadata = new HashMap<>();
                
                // Add quantity if not default (1)
                int quantity = itemStack.getQuantity();
                if (quantity > 1) {
                    metadata.put("count", quantity);
                }
                
                // Add durability if not at max
                double durability = itemStack.getDurability();
                double maxDurability = itemStack.getMaxDurability();
                if (durability < maxDurability && maxDurability > 0) {
                    metadata.put("durability", durability);
                    metadata.put("max_durability", maxDurability);
                }
                
                // Add metadata if present
                BsonDocument bsonMetadata = itemStack.getMetadata();
                if (bsonMetadata != null && !bsonMetadata.isEmpty()) {
                    // Convert BsonDocument to Map for JSON storage
                    Map<String, Object> customData = convertBsonToMap(bsonMetadata);
                    if (!customData.isEmpty()) {
                        metadata.put("custom_data", customData);
                    }
                }
                
                if (!metadata.isEmpty()) {
                    itemData.put("metadata", metadata);
                }
                
                inventoryData.put(String.valueOf(globalSlot), itemData);
            }
        }
    }

    private Map<String, Object> convertBsonToMap(BsonDocument bson) {
        Map<String, Object> map = new HashMap<>();
        if (bson == null) {
            return map;
        }
        
        for (String key : bson.keySet()) {
            org.bson.BsonValue value = bson.get(key);
            if (value != null) {
                if (value.isString()) {
                    map.put(key, value.asString().getValue());
                } else if (value.isInt32()) {
                    map.put(key, value.asInt32().getValue());
                } else if (value.isInt64()) {
                    map.put(key, value.asInt64().getValue());
                } else if (value.isDouble()) {
                    map.put(key, value.asDouble().getValue());
                } else if (value.isBoolean()) {
                    map.put(key, value.asBoolean().getValue());
                } else if (value.isDocument()) {
                    map.put(key, convertBsonToMap(value.asDocument()));
                } else if (value.isArray()) {
                    // Handle arrays - convert to string representation
                    map.put(key, value.asArray().toString());
                } else {
                    map.put(key, value.toString());
                }
            }
        }
        return map;
    }
}
