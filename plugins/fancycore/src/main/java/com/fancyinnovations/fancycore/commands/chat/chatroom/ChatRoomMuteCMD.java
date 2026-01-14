package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ChatRoomMuteCMD extends CommandBase {

    protected final OptionalArg<ChatRoom> chatRoomNameArg = this.withOptionalArg(ChatRoomArg.NAME, ChatRoomArg.DESCRIPTION, ChatRoomArg.TYPE);

    protected ChatRoomMuteCMD() {
        super("mute", "Mute the chat of a chat room");
        requirePermission("fancycore.commands.chatroom.mute");
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

        ChatRoom chatRoom = chatRoomNameArg.provided(ctx) ? chatRoomNameArg.get(ctx) : fp.getCurrentChatRoom();

        if (chatRoom.isMuted()) {
            fp.sendMessage("Chat for chat room " + chatRoom.getName() + " is already muted.");
            return;
        }

        chatRoom.setMuted(true);
        FancyCorePlugin.get().getChatStorage().setChatRoom(chatRoom);

        fp.sendMessage("Chat for chat room " + chatRoom.getName() + " has been muted.");
    }
}
