package com.fancyinnovations.fancycore.commands.chat.message;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.events.chat.PrivateMessageSentEvent;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> receiverArg = this.withRequiredArg("receiver", "The player to send the message to", FancyPlayerArg.TYPE);
    protected final RequiredArg<List<String>> messageArg = this.withListRequiredArg("message", "The message to send", ArgTypes.STRING);

    public MessageCMD() {
        super("message", "Send a private message to another player");
        addAliases("msg", "dm");
        requirePermission("fancycore.commands.message");
        setAllowsExtraArguments(true);
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


        FancyPlayer receiver = receiverArg.get(ctx);
        if (!receiver.isOnline()) {
            sender.sendMessage("The player " + receiver.getData().getUsername() + " is not online.");
            return;
        }

        if (receiver.getData().getUUID().equals(sender.getData().getUUID())) {
            sender.sendMessage("You cannot send a private message to yourself.");
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

        String[] parts = ctx.getInputString().split(" ");
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 2; i < parts.length; i++) {
            messageBuilder.append(parts[i]);
            if (i < parts.length - 1) {
                messageBuilder.append(" ");
            }
        }
        String message = messageBuilder.toString();

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
