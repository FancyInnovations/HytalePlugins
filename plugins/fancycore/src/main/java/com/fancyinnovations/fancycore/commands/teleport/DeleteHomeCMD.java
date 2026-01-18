package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.player.Home;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class DeleteHomeCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("", "Home name", ArgTypes.STRING);

    public DeleteHomeCMD() {
        super("deletehome", "Deletes your home point with the specified name");
        addAliases("delhome");
        requirePermission("fancycore.commands.deletehome");
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

        String homeName = nameArg.get(ctx);
        if (homeName == null || homeName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Home name cannot be empty."));
            return;
        }

        Home home = fp.getData().getHome(homeName);
        if (home == null) {
            ctx.sendMessage(Message.raw("Home \"" + homeName + "\" does not exist."));
            return;
        }

        fp.getData().removeHome(homeName);
        ctx.sendMessage(Message.raw("Home \"" + homeName + "\" deleted successfully."));
    }
}
