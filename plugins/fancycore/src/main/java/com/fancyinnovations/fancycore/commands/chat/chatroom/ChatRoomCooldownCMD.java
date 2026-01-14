package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ChatRoomCooldownCMD extends CommandBase {

    protected final RequiredArg<Float> cooldownArg = this.withRequiredArg("cooldown", "The cooldown in seconds", ArgTypes.FLOAT);
    protected final OptionalArg<ChatRoom> chatRoomNameArg = this.withOptionalArg(ChatRoomArg.NAME, ChatRoomArg.DESCRIPTION, ChatRoomArg.TYPE);

    protected ChatRoomCooldownCMD() {
        super("cooldown", "Set the chat cooldown for a chat room");
        requirePermission("fancycore.commands.chatroom.cooldown");
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

        long cooldownMs = (long) (cooldownArg.get(ctx) * 1000);
        chatRoom.setCooldown(cooldownMs);
        FancyCorePlugin.get().getChatStorage().setChatRoom(chatRoom);

        fp.sendMessage("Set cooldown of chat room " + chatRoom.getName() + " to " + TimeUtils.formatTime(cooldownMs));
    }
}
