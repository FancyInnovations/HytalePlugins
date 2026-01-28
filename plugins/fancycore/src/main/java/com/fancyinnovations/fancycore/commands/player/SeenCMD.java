package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class SeenCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg("player", "The player to check", FancyCoreArgs.PLAYER);

    public SeenCMD() {
        super("seen", "Displays the last seen time of a player");
        requirePermission("fancycore.commands.seen");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FancyPlayer target = targetArg.get(ctx);

        if (target.isOnline()) {
            String message = target.getData().getUsername() + " is currently online.";
            if (ctx.isPlayer()) {
                FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
                if (fp != null) {
                    fp.sendMessage(message);
                } else {
                    ctx.sendMessage(Message.raw(message));
                }
            } else {
                ctx.sendMessage(Message.raw(message));
            }
            return;
        }

        // Player is offline - use last login time
        long lastLoginTime = target.getData().getLastLoginTime();
        if (lastLoginTime <= 0) {
            // Fallback to first login time if last login time is not set (for old data)
            lastLoginTime = target.getData().getFirstLoginTime();
        }
        
        long timeSinceLastLogin = System.currentTimeMillis() - lastLoginTime;
        String formattedTime = TimeUtils.formatTime(timeSinceLastLogin);
        
        String message = target.getData().getUsername() + " was last seen " + formattedTime + " ago.";
        
        if (ctx.isPlayer()) {
            FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
            if (fp != null) {
                fp.sendMessage(message);
            } else {
                ctx.sendMessage(Message.raw(message));
            }
        } else {
            ctx.sendMessage(Message.raw(message));
        }
    }
}
