package com.fancyinnovations.fancycore.commands.inventory;

import com.fancyinnovations.fancycore.api.inventory.Backpack;
import com.fancyinnovations.fancycore.api.inventory.BackpacksService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerWindow;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BackpackCMD extends AbstractPlayerCommand {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("name", "backpack name", ArgTypes.STRING);
    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("player", "target player", FancyPlayerArg.TYPE);

    public BackpackCMD() {
        super("backpack", "Opens the specified backpack of the targeted player");
        requirePermission("fancycore.commands.backpack");
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

        String backpackName = nameArg.get(ctx);
        FancyPlayer target = targetArg.provided(ctx) ? targetArg.get(ctx) : fp;

        Backpack backpack = BackpacksService.get().getBackpack(target.getData().getUUID(), backpackName);
        if (backpack == null) {
            fp.sendMessage("Backpack '" + backpackName + "' not found for " + target.getData().getUsername() + ".");
            return;
        }

        List<ItemStack> items = BackpacksService.get().getBackpackItems(target.getData().getUUID(), backpackName);

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            fp.sendMessage("You are not a player.");
            return;
        }

        // Create a container with the backpack's capacity
        SimpleItemContainer backpackContainer = new SimpleItemContainer((short) backpack.size());
        
        // Load items into the container
        for (int i = 0; i < items.size() && i < backpack.size(); i++) {
            ItemStack item = items.get(i);
            if (item != null) {
                backpackContainer.setItemStackForSlot((short) i, item);
            }
        }

        UUID targetUUID = target.getData().getUUID();
        String finalBackpackName = backpackName;

        // Create and open the container window
        ContainerWindow window = new ContainerWindow(backpackContainer);
        
        // Register close event to save items back to storage
        window.registerCloseEvent(event -> {
            // Save all items from the container back to storage
            List<ItemStack> savedItems = new java.util.ArrayList<>();
            for (short i = 0; i < backpackContainer.getCapacity(); i++) {
                ItemStack item = backpackContainer.getItemStack(i);
                if (item != null) {
                    savedItems.add(item);
                }
            }
            BackpacksService.get().setBackpackItems(targetUUID, finalBackpackName, savedItems);
        });
        
        // Open the window using PageManager (this actually sends the packet)
        if (player.getPageManager().setPageWithWindows(ref, store, Page.Bench, true, window)) {
            fp.sendMessage("Opened backpack '" + backpackName + "' (" + backpack.size() + " slots) of " + target.getData().getUsername() + ".");
        } else {
            fp.sendMessage("Failed to open backpack '" + backpackName + "'.");
        }
    }
}
