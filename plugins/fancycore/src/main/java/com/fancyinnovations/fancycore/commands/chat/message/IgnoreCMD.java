package com.fancyinnovations.fancycore.commands.chat.message;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class IgnoreCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg("target", "The player to ignore", FancyPlayerArg.TYPE);

    public IgnoreCMD() {
        super("ignore", "Ignore a player to stop receiving their messages.");
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


        FancyPlayer target = targetArg.get(ctx);
        if (target.getData().getUUID().equals(fp.getData().getUUID())) {
            fp.sendMessage("You cannot ignore yourself.");
            return;
        }

        fp.getData().addIgnoredPlayer(target.getData().getUUID());

        fp.sendMessage("You are now ignoring " + target.getData().getUsername() + ".");
    }
}
