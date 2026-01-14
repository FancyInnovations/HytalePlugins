package com.fancyinnovations.fancycore.commands.chat.message;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ToggleMessagesCMD extends CommandBase {

    public ToggleMessagesCMD() {
        super("togglemessages", "Toggle receiving private messages from all players.");
        addAliases("toggledms");
        requirePermission("fancycore.commands.togglemessages");
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


        fp.getData().setPrivateMessagesEnabled(!fp.getData().isPrivateMessagesEnabled());

        String status = fp.getData().isPrivateMessagesEnabled() ? "enabled" : "disabled";
        fp.sendMessage("Private messages have been " + status + ".");
    }
}
