package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ChatRoomDeleteCMD extends CommandBase {

    protected final OptionalArg<ChatRoom> chatRoomNameArg = this.withOptionalArg(ChatRoomArg.NAME, ChatRoomArg.DESCRIPTION, ChatRoomArg.TYPE);

    protected ChatRoomDeleteCMD() {
        super("delete", "Deletes a chat room");
        requirePermission("fancycore.commands.chatroom.delete");
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

        // TODO: Permission check
//        if (!fp.checkPermission("fancycore.commands.chatroom.delete")) {
//            fp.sendMessage(Message.raw("You do not have permission to delete a chat room."));
//            return;
//        }

        ChatRoom chatRoom = chatRoomNameArg.provided(ctx) ? chatRoomNameArg.get(ctx) : fp.getCurrentChatRoom();

        ChatRoom defaultChatRoom = ChatService.get().getChatRoom(FancyCorePlugin.get().getConfig().getDefaultChatroom());
        if (defaultChatRoom.getName().equals(chatRoom.getName())) {
            fp.sendMessage("You cannot delete the default chat room.");
            return;
        }

        for (FancyPlayer onlinePlayer : FancyPlayerService.get().getOnlinePlayers()) {
            if (onlinePlayer.getCurrentChatRoom().getName().equals(chatRoom.getName())) {
                onlinePlayer.switchChatRoom(defaultChatRoom);
            }
        }

        ChatService.get().deleteChatRoom(chatRoom.getName());

        fp.sendMessage("Chat room " + chatRoom.getName() + " has been deleted.");
    }
}
