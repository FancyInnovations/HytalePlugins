package com.fancyinnovations.fancycore.commands.chat.message;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.events.chat.PrivateMessageSentEvent;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReplyCMD extends CommandBase {

    protected final RequiredArg<List<String>> messageArg = this.withListRequiredArg("message", "The message to send", ArgTypes.STRING);

    public ReplyCMD() {
        super("reply", "Reply to the last player who sent you a private message");
        requirePermission("fancycore.commands.reply");
        addAliases("r");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer sender = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (sender == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }


        FancyPlayer receiver = sender.getReplyTo();
        if (receiver == null) {
            sender.sendMessage("You have no one to reply to.");
            return;
        }
        if (!receiver.isOnline()) {
            sender.sendMessage("The player " + receiver.getData().getUsername() + " is not online.");
            return;
        }

        if (!receiver.getData().isPrivateMessagesEnabled()) {
            sender.sendMessage("The player " + receiver.getData().getUsername() + " is not accepting private messages.");
            return;
        }

        if (receiver.getData().getIgnoredPlayers().contains(sender.getData().getUUID())) {
            sender.sendMessage("You cannot send a private message to " + receiver.getData().getUsername() + " because they are ignoring you.");
            return;
        }

        String message = String.join(" ", messageArg.get(ctx));

        if (!new PrivateMessageSentEvent(sender, receiver, message).fire()) {
            return;
        }

        String parsedMessage = FancyCore.get().getConfig().getPrivateMessageFormat()
                .replace("%sender%", sender.getData().getUsername())
                .replace("%receiver%", receiver.getData().getUsername())
                .replace("%message%", message);

        receiver.sendMessage(parsedMessage);
        sender.sendMessage(parsedMessage);

        sender.setReplyTo(receiver);
        receiver.setReplyTo(sender);
    }
}
