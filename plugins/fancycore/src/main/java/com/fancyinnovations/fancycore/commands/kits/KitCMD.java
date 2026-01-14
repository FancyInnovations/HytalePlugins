package com.fancyinnovations.fancycore.commands.kits;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.fancyinnovations.fancycore.kits.storage.KitStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class KitCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("", "Kit name", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
    protected final OptionalArg<FancyPlayer> playerArg = this.withOptionalArg("", "Target player", FancyPlayerArg.TYPE);

    public KitCMD() {
        super("kit", "Gives the specified kit to the targeted player(s)");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        String kitName = nameArg.get(ctx);
        if (kitName == null || kitName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Kit name cannot be empty."));
            return;
        }

        KitStorage kitStorage = com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getKitStorage();

        // Check if kit exists
        Map<String, Object> kitData = kitStorage.getKit(kitName);
        if (kitData == null) {
            ctx.sendMessage(Message.raw("Kit \"" + kitName + "\" does not exist."));
            return;
        }

        // Get sender's FancyPlayer for permission check
        FancyPlayer senderPlayer = null;
        if (ctx.isPlayer()) {
            senderPlayer = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
            if (senderPlayer == null) {
                ctx.sendMessage(Message.raw("FancyPlayer not found."));
                return;
            }
            
            // Check permission for this specific kit on the sender
            // Allow specific kit permission, wildcard permission, base permission, or global wildcard (*)
            String kitPermission = "fancycore.commands.kit." + kitName.toLowerCase();
            String wildcardPermission = "fancycore.commands.kit.*";
            String basePermission = "fancycore.commands.kit";
            
            boolean hasPermission = senderPlayer.checkPermission(kitPermission) || 
                                    senderPlayer.checkPermission(wildcardPermission) || 
                                    senderPlayer.checkPermission(basePermission) ||
                                    senderPlayer.checkPermission("*"); // Global wildcard
            
            if (!hasPermission) {
                ctx.sendMessage(Message.raw("You do not have permission to use kit \"" + kitName + "\"."));
                return;
            }
        }

        // Determine target player
        FancyPlayer targetPlayer;
        if (playerArg.provided(ctx)) {
            // Target specified player
            targetPlayer = playerArg.get(ctx);
            if (targetPlayer == null) {
                ctx.sendMessage(Message.raw("Target player not found."));
                return;
            }
            
            // Check if sender has permission to give kit to others
            if (senderPlayer != null && 
                !senderPlayer.checkPermission("fancycore.commands.kit.others") && 
                !senderPlayer.checkPermission("*")) { // Global wildcard
                ctx.sendMessage(Message.raw("You do not have permission to give kits to other players."));
                return;
            }
        } else {
            // Target sender
            if (!ctx.isPlayer()) {
                ctx.sendMessage(Message.raw("This command can only be executed by a player when no target is specified."));
                return;
            }
            
            targetPlayer = senderPlayer;
            if (targetPlayer == null) {
                ctx.sendMessage(Message.raw("FancyPlayer not found."));
                return;
            }
        }

        PlayerRef targetPlayerRef = targetPlayer.getPlayer();
        if (targetPlayerRef == null) {
            ctx.sendMessage(Message.raw("Target player is not online."));
            return;
        }

        Ref<EntityStore> targetRef = targetPlayerRef.getReference();
        if (targetRef == null || !targetRef.isValid()) {
            ctx.sendMessage(Message.raw("Target player is not in a world."));
            return;
        }

        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = ((EntityStore) targetStore.getExternalData()).getWorld();

        // Execute on the world thread to access inventory
        targetWorld.execute(() -> {
            Player player = (Player) targetStore.getComponent(targetRef, Player.getComponentType());
            if (player == null) {
                ctx.sendMessage(Message.raw("Failed to get player component."));
                return;
            }

            Inventory inventory = player.getInventory();
            if (inventory == null) {
                ctx.sendMessage(Message.raw("Failed to access target player's inventory."));
                return;
            }

            // Get inventory data from kit
            @SuppressWarnings("unchecked")
            Map<String, Object> inventoryData = (Map<String, Object>) kitData.get("inventory");
            if (inventoryData == null || inventoryData.isEmpty()) {
                ctx.sendMessage(Message.raw("Kit \"" + kitName + "\" is empty."));
                return;
            }

            // Give items to player
            int itemsGiven = giveItemsToPlayer(inventory, inventoryData);

            String targetName = targetPlayer.getData().getUsername();
            if (targetPlayer.getData().getUUID().equals(ctx.sender().getUuid())) {
                ctx.sendMessage(Message.raw("Kit \"" + kitName + "\" given to you (" + itemsGiven + " items)."));
            } else {
                ctx.sendMessage(Message.raw("Kit \"" + kitName + "\" given to " + targetName + " (" + itemsGiven + " items)."));
            }
        });
    }

    private int giveItemsToPlayer(Inventory inventory, Map<String, Object> inventoryData) {
        int itemsGiven = 0;
        
        ItemContainer storage = inventory.getStorage();
        ItemContainer hotbar = inventory.getHotbar();
        ItemContainer armor = inventory.getArmor();
        ItemContainer utility = inventory.getUtility();
        ItemContainer backpack = inventory.getBackpack();
        ItemContainer tools = inventory.getTools();

        int storageCapacity = storage.getCapacity();
        int hotbarCapacity = hotbar.getCapacity();
        int armorCapacity = armor.getCapacity();
        int utilityCapacity = utility.getCapacity();
        int backpackCapacity = backpack.getCapacity();

        for (Map.Entry<String, Object> entry : inventoryData.entrySet()) {
            try {
                int slot = Integer.parseInt(entry.getKey());
                @SuppressWarnings("unchecked")
                Map<String, Object> itemData = (Map<String, Object>) entry.getValue();
                
                // Determine which container this slot belongs to
                ItemContainer container = null;
                short containerSlot = 0;
                
                if (slot < storageCapacity) {
                    container = storage;
                    containerSlot = (short) slot;
                } else if (slot < storageCapacity + hotbarCapacity) {
                    container = hotbar;
                    containerSlot = (short) (slot - storageCapacity);
                } else if (slot < storageCapacity + hotbarCapacity + armorCapacity) {
                    container = armor;
                    containerSlot = (short) (slot - storageCapacity - hotbarCapacity);
                } else if (slot < storageCapacity + hotbarCapacity + armorCapacity + utilityCapacity) {
                    container = utility;
                    containerSlot = (short) (slot - storageCapacity - hotbarCapacity - armorCapacity);
                } else if (slot < storageCapacity + hotbarCapacity + armorCapacity + utilityCapacity + backpackCapacity) {
                    container = backpack;
                    containerSlot = (short) (slot - storageCapacity - hotbarCapacity - armorCapacity - utilityCapacity);
                } else {
                    container = tools;
                    containerSlot = (short) (slot - storageCapacity - hotbarCapacity - armorCapacity - utilityCapacity - backpackCapacity);
                }

                if (container != null) {
                    ItemStack itemStack = createItemStackFromData(itemData);
                    if (itemStack != null) {
                        container.setItemStackForSlot(containerSlot, itemStack);
                        itemsGiven++;
                    }
                }
            } catch (NumberFormatException e) {
                // Skip invalid slot numbers
            }
        }
        
        return itemsGiven;
    }

    private ItemStack createItemStackFromData(Map<String, Object> itemData) {
        try {
            String identifier = (String) itemData.get("identifier");
            if (identifier == null || identifier.isEmpty()) {
                return null;
            }

            // Get quantity (default to 1)
            int quantity = 1;
            double durability = 0;
            double maxDurability = 0;
            BsonDocument metadata = null;

            @SuppressWarnings("unchecked")
            Map<String, Object> metadataMap = (Map<String, Object>) itemData.get("metadata");
            if (metadataMap != null) {
                // Get quantity
                if (metadataMap.containsKey("count")) {
                    Object countObj = metadataMap.get("count");
                    if (countObj instanceof Number) {
                        quantity = ((Number) countObj).intValue();
                    }
                }

                // Get durability
                if (metadataMap.containsKey("durability")) {
                    Object durObj = metadataMap.get("durability");
                    if (durObj instanceof Number) {
                        durability = ((Number) durObj).doubleValue();
                    }
                }

                // Get max durability
                if (metadataMap.containsKey("max_durability")) {
                    Object maxDurObj = metadataMap.get("max_durability");
                    if (maxDurObj instanceof Number) {
                        maxDurability = ((Number) maxDurObj).doubleValue();
                    }
                }

                // Get custom data and convert to BsonDocument
                if (metadataMap.containsKey("custom_data")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> customData = (Map<String, Object>) metadataMap.get("custom_data");
                    if (customData != null && !customData.isEmpty()) {
                        metadata = convertMapToBson(customData);
                    }
                }
            }

            // Create ItemStack
            if (maxDurability > 0) {
                return new ItemStack(identifier, quantity, durability, maxDurability, metadata);
            } else {
                return new ItemStack(identifier, quantity, metadata);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private BsonDocument convertMapToBson(Map<String, Object> map) {
        BsonDocument bson = new BsonDocument();
        if (map == null) {
            return bson;
        }
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value == null) {
                continue;
            } else if (value instanceof String) {
                bson.append(key, new org.bson.BsonString((String) value));
            } else if (value instanceof Integer) {
                bson.append(key, new org.bson.BsonInt32((Integer) value));
            } else if (value instanceof Long) {
                bson.append(key, new org.bson.BsonInt64((Long) value));
            } else if (value instanceof Double) {
                bson.append(key, new org.bson.BsonDouble((Double) value));
            } else if (value instanceof Boolean) {
                bson.append(key, new org.bson.BsonBoolean((Boolean) value));
            } else if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                bson.append(key, convertMapToBson(nestedMap));
            } else {
                // Fallback to string representation
                bson.append(key, new org.bson.BsonString(value.toString()));
            }
        }
        return bson;
    }
}
