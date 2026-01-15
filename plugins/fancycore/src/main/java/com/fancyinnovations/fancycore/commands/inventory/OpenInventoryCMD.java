package com.fancyinnovations.fancycore.commands.inventory;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerWindow;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer.ItemContainerChangeEvent;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class OpenInventoryCMD extends AbstractPlayerCommand {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg("player", "target player", FancyPlayerArg.TYPE);

    public OpenInventoryCMD() {
        super("openinventory", "Opens the inventory of the targeted player");
        addAliases("invsee");
        requirePermission("fancycore.commands.openinventory");
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        FancyPlayer target = targetArg.get(ctx);
        
        if (!target.isOnline()) {
            fp.sendMessage("The player " + target.getData().getUsername() + " is not online.");
            return;
        }

        PlayerRef targetPlayerRef = target.getPlayer();
        if (targetPlayerRef == null) {
            fp.sendMessage("The player " + target.getData().getUsername() + " is not online.");
            return;
        }

        Ref<EntityStore> targetRef = targetPlayerRef.getReference();
        if (targetRef == null || !targetRef.isValid()) {
            fp.sendMessage("The player " + target.getData().getUsername() + " is not in a world.");
            return;
        }

        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = ((EntityStore) targetStore.getExternalData()).getWorld();

        targetWorld.execute(() -> {
            Player targetPlayer = targetStore.getComponent(targetRef, Player.getComponentType());
            if (targetPlayer == null) {
                world.execute(() -> {
                    fp.sendMessage("Failed to get player component for " + target.getData().getUsername() + ".");
                });
                return;
            }

            Player senderPlayer = store.getComponent(ref, Player.getComponentType());
            if (senderPlayer == null) {
                world.execute(() -> {
                    fp.sendMessage("You are not a player.");
                });
                return;
            }

            // Create a container with the target's inventory items
            ItemContainer targetStorage = targetPlayer.getInventory().getStorage();
            ItemContainer targetHotbar = targetPlayer.getInventory().getHotbar();
            
            // Create a combined container for viewing
            short totalCapacity = (short) (targetStorage.getCapacity() + targetHotbar.getCapacity());
            SimpleItemContainer viewContainer = new SimpleItemContainer(totalCapacity);
            
            // Copy hotbar items first
            for (short i = 0; i < targetHotbar.getCapacity(); i++) {
                ItemStack item = targetHotbar.getItemStack(i);
                if (item != null) {
                    viewContainer.setItemStackForSlot(i, item);
                }
            }
            
            // Copy storage items after hotbar
            for (short i = 0; i < targetStorage.getCapacity(); i++) {
                ItemStack item = targetStorage.getItemStack(i);
                if (item != null) {
                    viewContainer.setItemStackForSlot((short)(targetHotbar.getCapacity() + i), item);
                }
            }

            // Create and open the container window
            ContainerWindow window = new ContainerWindow(viewContainer);
            
            // Store references for the sync logic
            short hotbarCapacity = targetHotbar.getCapacity();
            short storageCapacity = targetStorage.getCapacity();
            
            // Function to sync a specific slot from viewContainer to target inventory
            java.util.function.Consumer<Short> syncSlot = (Short viewSlot) -> {
                targetWorld.execute(() -> {
                    // Get the target player again to ensure we have the latest instance
                    Player targetPlayerForSync = targetStore.getComponent(targetRef, Player.getComponentType());
                    if (targetPlayerForSync == null) {
                        return;
                    }
                    
                    ItemContainer targetStorageSync = targetPlayerForSync.getInventory().getStorage();
                    ItemContainer targetHotbarSync = targetPlayerForSync.getInventory().getHotbar();
                    
                    ItemStack item = viewContainer.getItemStack(viewSlot);
                    
                    if (viewSlot < hotbarCapacity) {
                        // This is a hotbar slot
                        targetHotbarSync.setItemStackForSlot(viewSlot, item);
                    } else {
                        // This is a storage slot
                        short storageSlot = (short)(viewSlot - hotbarCapacity);
                        if (storageSlot < targetStorageSync.getCapacity()) {
                            targetStorageSync.setItemStackForSlot(storageSlot, item);
                        }
                    }
                });
            };
            
            // Register change event to sync items in real-time as they're modified
            EventRegistration changeRegistration = viewContainer.registerChangeEvent((ItemContainerChangeEvent changeEvent) -> {
                // Get the transaction to see which slots were affected
                com.hypixel.hytale.server.core.inventory.transaction.Transaction transaction = changeEvent.transaction();
                
                // Sync all affected slots - for simplicity, we'll sync the entire container
                // when any change occurs (could be optimized to only sync affected slots)
                // But for real-time updates, syncing all is safer
                for (short i = 0; i < hotbarCapacity && i < viewContainer.getCapacity(); i++) {
                    syncSlot.accept(i);
                }
                for (short i = hotbarCapacity; i < viewContainer.getCapacity(); i++) {
                    syncSlot.accept(i);
                }
            });
            
            // Register close event to do a final sync and clean up the listener
            window.registerCloseEvent(event -> {
                // Unregister the change listener
                changeRegistration.unregister();
                
                // Final sync on close (as a safety measure)
                targetWorld.execute(() -> {
                    // Get the target player again to ensure we have the latest instance
                    Player targetPlayerForSync = targetStore.getComponent(targetRef, Player.getComponentType());
                    if (targetPlayerForSync == null) {
                        return;
                    }
                    
                    ItemContainer targetStorageSync = targetPlayerForSync.getInventory().getStorage();
                    ItemContainer targetHotbarSync = targetPlayerForSync.getInventory().getHotbar();
                    
                    // Sync hotbar items back from viewContainer
                    for (short i = 0; i < hotbarCapacity && i < viewContainer.getCapacity(); i++) {
                        ItemStack item = viewContainer.getItemStack(i);
                        targetHotbarSync.setItemStackForSlot(i, item);
                    }
                    
                    // Sync storage items back from viewContainer
                    for (short i = 0; i < storageCapacity; i++) {
                        short viewSlot = (short)(hotbarCapacity + i);
                        ItemStack item = null;
                        if (viewSlot < viewContainer.getCapacity()) {
                            item = viewContainer.getItemStack(viewSlot);
                        }
                        targetStorageSync.setItemStackForSlot(i, item);
                    }
                });
            });
            
            // Open the window using PageManager (this actually sends the packet)
            if (senderPlayer.getPageManager().setPageWithWindows(ref, store, Page.Bench, true, window)) {
                world.execute(() -> {
                    fp.sendMessage("Opened inventory of " + target.getData().getUsername() + ".");
                });
            } else {
                world.execute(() -> {
                    fp.sendMessage("Failed to open inventory of " + target.getData().getUsername() + ".");
                });
            }
        });
    }
}
