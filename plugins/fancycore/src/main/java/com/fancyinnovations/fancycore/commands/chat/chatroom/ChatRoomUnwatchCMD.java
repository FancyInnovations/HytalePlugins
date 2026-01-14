package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ChatRoomUnwatchCMD extends CommandBase {

    protected final RequiredArg<ChatRoom> chatRoomNameArg = this.withRequiredArg(ChatRoomArg.NAME, ChatRoomArg.DESCRIPTION, ChatRoomArg.TYPE);

    protected ChatRoomUnwatchCMD() {
        super("unwatch", "Stop watching a chat room. You will no longer receive messages from this chat room");
        requirePermission("fancycore.commands.chatroom.unwatch");
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

        ChatRoom chatRoom = chatRoomNameArg.get(ctx);

        String defaultChatroom = FancyCorePlugin.get().getConfig().getDefaultChatroom();
        if (chatRoom.getName().equalsIgnoreCase(defaultChatroom)) {
            fp.sendMessage("You cannot unwatch the default chat room.");
            return;
        }

        if (!chatRoom.getWatchers().contains(fp)) {
            fp.sendMessage("You are not watching chat room " + chatRoom.getName() + ".");
            return;
        }

        chatRoom.stopWatching(fp);

        fp.sendMessage("You have stopped watching chat room " + chatRoom.getName() + ".");
    }
}
