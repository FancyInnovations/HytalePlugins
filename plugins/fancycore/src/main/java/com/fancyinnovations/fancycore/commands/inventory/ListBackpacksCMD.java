package com.fancyinnovations.fancycore.commands.inventory;

import com.fancyinnovations.fancycore.api.inventory.Backpack;
import com.fancyinnovations.fancycore.api.inventory.BackpacksService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListBackpacksCMD extends CommandBase {

    public ListBackpacksCMD() {
        super("listbackpacks", "Lists all available backpacks");
        addAliases("backpacks");
        requirePermission("fancycore.commands.listbackpacks");
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

        List<Backpack> backpacks = BackpacksService.get().getBackpacks(fp.getData().getUUID());

        if (backpacks.isEmpty()) {
            ctx.sendMessage(Message.raw("You have no backpacks."));
            return;
        }

        ctx.sendMessage(Message.raw("Your Backpacks:"));
        for (Backpack backpack : backpacks) {
            ctx.sendMessage(Message.raw("- " + backpack.name() + " (" + backpack.size() + " slots)"));
        }
    }
}
