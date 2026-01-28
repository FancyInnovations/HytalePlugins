package com.fancyinnovations.fancycore.commands.permissions;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class DeopCMD extends CommandBase {
    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg("target", "username or uuid of the target player", FancyCoreArgs.PLAYER);

    public DeopCMD() {
        super("deop", "Revokes all permissions from a player");
        requirePermission("fancycore.commands.permissions.set");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FancyPlayer target = targetArg.get(ctx);
        target.getData().removePermission("*");

        ctx.sendMessage(Message.raw("Revoked all permissions to player " + target.getData().getUsername() + "."));
    }
}
