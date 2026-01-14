package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ChatRoomCreateCMD extends CommandBase {

    protected final RequiredArg<String> chatRoomNameArg = this.withRequiredArg(ChatRoomArg.NAME, ChatRoomArg.DESCRIPTION, ArgTypes.STRING);

    protected ChatRoomCreateCMD() {
        super("create", "Create a new chat room");
        requirePermission("fancycore.commands.chatroom.create");
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
//        if (!fp.checkPermission("fancycore.commands.chatroom.create")) {
//            fp.sendMessage(Message.raw("You do not have permission to create chat rooms."));
//            return;
//        }

        String name = chatRoomNameArg.get(ctx);

        if (ChatService.get().getChatRoom(name) != null) {
            fp.sendMessage("A chat room with the name " + name + " already exists.");
            return;
        }

        ChatRoom chatRoom = ChatService.get().createChatRoom(name);
        chatRoom.startWatching(fp);

        fp.sendMessage("Chat room " + name + " has been created and you are now watching it.");
    }
}
