package com.fancyinnovations.fancycore.commands.inventory;

import com.fancyinnovations.fancycore.api.inventory.Backpack;
import com.fancyinnovations.fancycore.api.inventory.BackpacksService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class DeleteBackpackCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("name", "backpack name", ArgTypes.STRING);

    public DeleteBackpackCMD() {
        super("deletebackpack", "Deletes the specified backpack");
        requirePermission("fancycore.commands.deletebackpack");
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

        Backpack backpack = BackpacksService.get().getBackpack(fp.getData().getUUID(), name);
        if (backpack == null) {
            ctx.sendMessage(Message.raw("Backpack '" + name + "' not found."));
            return;
        }

        BackpacksService.get().deleteBackpack(fp.getData().getUUID(), name);
        ctx.sendMessage(Message.raw("Deleted backpack '" + name + "'."));
    }
}
