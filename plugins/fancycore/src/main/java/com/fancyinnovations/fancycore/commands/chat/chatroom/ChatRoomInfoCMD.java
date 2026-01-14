package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatRoomInfoCMD extends CommandBase {

    protected final OptionalArg<ChatRoom> chatRoomNameArg = this.withOptionalArg(ChatRoomArg.NAME, ChatRoomArg.DESCRIPTION, ChatRoomArg.TYPE);

    protected ChatRoomInfoCMD() {
        super("info", "Get information about a chat room");
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

        List<String> nicknames = chatRoom.getWatchers().stream()
                .map(fancyPlayer -> fancyPlayer.getData().getNickname())
                .toList();

        fp.sendMessage("Chat Room Info:");
        fp.sendMessage("- Name: " + chatRoom.getName());
        fp.sendMessage("- IsMuted: " + chatRoom.isMuted());
        fp.sendMessage("- Cooldown: " + TimeUtils.formatTime(chatRoom.getCooldown()));
        fp.sendMessage("- Watchers: " + chatRoom.getWatchers().size() + " (" + String.join(", ", nicknames) + ")");
    }
}
