package com.fancyinnovations.fancycore.commands.inventory;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class ClearInventoryCMD extends AbstractPlayerCommand {

    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("player", "target player", FancyCoreArgs.PLAYER);

    public ClearInventoryCMD() {
        super("clearinventory", "Clears the inventory of the targeted player(s)");
        addAliases("ci");
        requirePermission("fancycore.commands.clearinventory");
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

        FancyPlayer target = targetArg.provided(ctx) ? targetArg.get(ctx) : fp;

        if (target.getData().getUUID() != fp.getData().getUUID() && !PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.commands.clearinventory.others")) {
            fp.sendMessage("You do not have permission to clear other players' inventories.");
            return;
        }
        
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
            Player player = targetStore.getComponent(targetRef, Player.getComponentType());
            if (player == null) {
                fp.sendMessage("Failed to get player component for " + target.getData().getUsername() + ".");
                return;
            }

            ItemContainer hotbar = player.getInventory().getHotbar();
            ItemContainer storage = player.getInventory().getStorage();

            // Clear hotbar
            for (short i = 0; i < hotbar.getCapacity(); i++) {
                ItemStack item = hotbar.getItemStack(i);
                if (item != null) {
                    hotbar.removeItemStack(item);
                }
            }

            // Clear storage
            for (short i = 0; i < storage.getCapacity(); i++) {
                ItemStack item = storage.getItemStack(i);
                if (item != null) {
                    storage.removeItemStack(item);
                }
            }

            if (target.getData().getUUID().equals(fp.getData().getUUID())) {
                fp.sendMessage("Your inventory has been cleared.");
            } else {
                fp.sendMessage("Cleared inventory of " + target.getData().getUsername() + ".");
                target.sendMessage("Your inventory has been cleared by " + fp.getData().getUsername() + ".");
            }
        });
    }
}
