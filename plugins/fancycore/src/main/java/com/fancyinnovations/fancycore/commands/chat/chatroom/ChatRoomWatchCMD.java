package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ChatRoomWatchCMD extends CommandBase {

    protected final RequiredArg<ChatRoom> chatRoomNameArg = this.withRequiredArg(ChatRoomArg.NAME, ChatRoomArg.DESCRIPTION, ChatRoomArg.TYPE);

    protected ChatRoomWatchCMD() {
        super("watch", "Start watching a chat room. You will receive messages from this chat room");
        requirePermission("fancycore.commands.chatroom.watch");
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

        // TODO: Permission check
//        if (!fp.checkPermission("fancycore.chatroom."+chatRoom.getName())) {
//            fp.sendMessage(Message.raw("You do not have permission to watch chat room " + chatRoom.getName() + "."));
//            return;
//        }

        if (chatRoom.getWatchers().contains(fp)) {
            fp.sendMessage("You are already watching chat room " + chatRoom.getName() + ".");
            return;
        }

        chatRoom.startWatching(fp);

        fp.sendMessage("You are now watching chat room " + chatRoom.getName() + ".");
    }
}
