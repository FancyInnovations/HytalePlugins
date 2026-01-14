package com.fancyinnovations.fancycore.commands.chat.message;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class UnignoreCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg("target", "The player to unignore", FancyPlayerArg.TYPE);

    public UnignoreCMD() {
        super("unignore", "Unignore a player to start receiving their messages again.");
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

        if (!fp.getData().getIgnoredPlayers().contains(target.getData().getUUID())) {
            ctx.sendMessage(Message.raw("You are not ignoring " + target.getData().getUsername() + "."));
            return;
        }

        fp.getData().removeIgnoredPlayer(target.getData().getUUID());

        ctx.sendMessage(Message.raw("You have unignored " + target.getData().getUsername() + "."));
    }
}
