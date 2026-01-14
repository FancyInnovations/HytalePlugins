package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatRoomWatchingCMD extends CommandBase {

    protected ChatRoomWatchingCMD() {
        super("watching", "Show information about the chat rooms you are currently watching");
        requirePermission("fancycore.commands.chatroom.watching");
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

        List<ChatRoom> watchingRooms = ChatService.get().getAllChatRooms().stream()
                .filter(chatRoom -> chatRoom.getWatchers().contains(fp))
                .toList();

        fp.sendMessage("You are currently watching " + watchingRooms.size() + " chat room(s):");
        for (ChatRoom cr : watchingRooms) {
            fp.sendMessage("- " + cr.getName() + ":");
        }

        ChatRoom currentChatRoom = fp.getCurrentChatRoom();
        if (currentChatRoom != null) {
            fp.sendMessage("Your current active chat room is: " + currentChatRoom.getName());
        }
    }
}
