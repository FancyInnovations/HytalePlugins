package com.fancyinnovations.fancycore.commands.inventory;

import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.fancycore.inventory.service.KitsServiceImpl;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreateKitCMD extends AbstractPlayerCommand {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("name", "name for the new kit", ArgTypes.STRING);
    protected final OptionalArg<Long> cooldownArg = this.withOptionalArg("cooldown", "cooldown for the kit", FancyCoreArgs.DURATION);

    public CreateKitCMD() {
        super("createkit", "Creates a new kit with the specified name (note: it contains all items in your inventory)");
        requirePermission("fancycore.commands.createkit");
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

        String name = nameArg.get(ctx);
        if (KitsService.get().getKit(name) != null) {
            fp.sendMessage("A kit with this name already exists");
            return;
        }

        long cooldown = cooldownArg.provided(ctx) ? cooldownArg.get(ctx) : -1;

        Player player = ref.getStore().getComponent(ref, Player.getComponentType());
        if (player == null) {
            fp.sendMessage("You are not an player");
            return;
        }
        
        ItemContainer hotbar = player.getInventory().getHotbar();
        ItemContainer storage = player.getInventory().getStorage();
        ItemContainer armor = player.getInventory().getArmor();

        // Store items in order to preserve slot positions
        List<ItemStack> hotbarItems = new ArrayList<>();
        for (short i = 0; i < hotbar.getCapacity(); i++) {
            hotbarItems.add(hotbar.getItemStack(i)); // Include nulls to preserve slot positions
        }

        List<ItemStack> storageItems = new ArrayList<>();
        for (short i = 0; i < storage.getCapacity(); i++) {
            storageItems.add(storage.getItemStack(i)); // Include nulls to preserve slot positions
        }

        List<ItemStack> armorItems = new ArrayList<>();
        for (short i = 0; i < armor.getCapacity(); i++) {
            armorItems.add(armor.getItemStack(i)); // Include nulls to preserve slot positions
        }

        Kit kit = new Kit(name, name, name, cooldown);

        // Use the new method if available, otherwise fallback to old method
        if (KitsService.get() instanceof KitsServiceImpl) {
            ((KitsServiceImpl) KitsService.get()).createKitWithContainers(kit, hotbarItems, storageItems, armorItems);
        } else {
            // Fallback: combine items
            List<ItemStack> allItems = new ArrayList<>(hotbarItems);
            allItems.addAll(storageItems);
            allItems.addAll(armorItems);
            KitsService.get().createKit(kit, allItems);
        }

        fp.sendMessage("Created kit "+kit.name());
    }
}
