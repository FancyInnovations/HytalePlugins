package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.protocol.packets.connection.PongType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

public class PingCMD extends CommandBase {

    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("target", "The player to check ping for", FancyCoreArgs.PLAYER);

    public PingCMD() {
        super("ping", "Displays the ping of a player");
        requirePermission("fancycore.commands.ping");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null && ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        FancyPlayer target = targetArg.provided(ctx) ? targetArg.get(ctx) : fp;

        if (target == null) {
            ctx.sendMessage(Message.raw("Target player not found."));
            return;
        }

        if (!target.isOnline()) {
            String message = "The player " + target.getData().getUsername() + " is not online.";
            if (ctx.isPlayer() && fp != null) {
                fp.sendMessage(message);
            } else {
                ctx.sendMessage(Message.raw(message));
            }
            return;
        }

        PlayerRef targetPlayerRef = target.getPlayer();
        if (targetPlayerRef == null) {
            String message = "The player " + target.getData().getUsername() + " is not online.";
            if (ctx.isPlayer() && fp != null) {
                fp.sendMessage(message);
            } else {
                ctx.sendMessage(Message.raw(message));
            }
            return;
        }

        PacketHandler.PingInfo pingInfo = targetPlayerRef.getPacketHandler().getPingInfo(PongType.Raw);
        com.hypixel.hytale.metrics.metric.HistoricMetric pingMetricSet = pingInfo.getPingMetricSet();
        double averagePing = pingMetricSet.getAverage(1);
        long pingMillis = PacketHandler.PingInfo.TIME_UNIT.toMillis(Math.round(averagePing));

        String message;
        boolean isSelf = fp != null && target.getData() != null && fp.getData() != null
                && target.getData().getUUID().equals(fp.getData().getUUID());
        if (isSelf) {
            message = "Your ping is " + pingMillis + "ms.";
        } else {
            message = target.getData().getUsername() + "'s ping is " + pingMillis + "ms.";
        }

        if (ctx.isPlayer() && fp != null) {
            fp.sendMessage(message);
        } else {
            ctx.sendMessage(Message.raw(message));
        }
    }
}
