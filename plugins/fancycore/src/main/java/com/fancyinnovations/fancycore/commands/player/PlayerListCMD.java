package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class PlayerListCMD extends CommandBase {


    public PlayerListCMD() {
        super("playerlist", "Displays a list of all online players");
        addAliases("online", "list");
        requirePermission("fancycore.commands.playerlist");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        ctx.sendMessage(Message.raw("Online Players:"));

        for (FancyPlayer onlinePlayer : FancyPlayerService.get().getOnlinePlayers()) {
            ctx.sendMessage(Message.raw("- " + onlinePlayer.getData().getUsername() + " (UUID: " + onlinePlayer.getData().getUUID().toString() + ")"));
        }
    }
}
