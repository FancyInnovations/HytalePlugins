package com.fancyinnovations.fancycore.commands.inventory;

import com.fancyinnovations.fancycore.api.inventory.Backpack;
import com.fancyinnovations.fancycore.api.inventory.BackpacksService;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class CreateBackpackCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("name", "backpack name", ArgTypes.STRING);
    protected final OptionalArg<Integer> sizeArg = this.withOptionalArg("size", "inventory size", ArgTypes.INTEGER);

    public CreateBackpackCMD() {
        super("createbackpack", "Creates a new backpack with the specified name and size");
        addAliases("createbp", "createvirtualbackpack");
        requirePermission("fancycore.commands.createbackpack");
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

        String name = nameArg.get(ctx);
        int size = sizeArg.provided(ctx) ? sizeArg.get(ctx) : 54;
        if (size <= 0 || size > 54) {
            ctx.sendMessage(Message.raw("Backpack size must be between 1 and 54."));
            return;
        }

        int maxBackpacks = -1;
        for (Group group : fp.getData().getGroupSortedByWeight()) {
            Object val = group.getMetadataValueInherited("max_backpacks");
            if (val != null) {
                maxBackpacks = (int) ((double) val);
                break;
            }
        }
        if (maxBackpacks != -1) {
            int currentBackpacks = BackpacksService.get().getBackpacks(fp.getData().getUUID()).size();
            if (currentBackpacks >= maxBackpacks) {
                ctx.sendMessage(Message.raw("You have reached the maximum number of backpacks (" + maxBackpacks + ")."));
                return;
            }
        }

        Backpack existing = BackpacksService.get().getBackpack(fp.getData().getUUID(), name);
        if (existing != null) {
            ctx.sendMessage(Message.raw("A backpack with the name '" + name + "' already exists."));
            return;
        }

        BackpacksService.get().createBackpack(fp.getData().getUUID(), name, size);
        ctx.sendMessage(Message.raw("Created backpack '" + name + "' with " + size + " slots."));
    }
}
