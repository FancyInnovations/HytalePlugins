package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class PlaytimeCMD extends CommandBase {

    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("target", "The player to check playtime for", FancyCoreArgs.PLAYER);

    public PlaytimeCMD() {
        super("playtime", "Check your or another player's playtime");
        requirePermission("fancycore.commands.playtime");
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

        FancyPlayer target = targetArg.provided(ctx) ? targetArg.get(ctx) : fp;

        long playtime = target.getData().getPlayTime();
        if (target.isOnline()) {
            playtime += System.currentTimeMillis() - target.getJoinedAt();
        }
        String formattedPlaytime = TimeUtils.formatTime(playtime);

        if (target.getData().getUUID().equals(fp.getData().getUUID())) {
            fp.sendMessage("Your total playtime is: " + formattedPlaytime);
        } else {
            fp.sendMessage(target.getData().getUsername() + "'s total playtime is: " + formattedPlaytime);
        }
    }
}
