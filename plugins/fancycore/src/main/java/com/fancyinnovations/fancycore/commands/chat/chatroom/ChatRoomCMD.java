package com.fancyinnovations.fancycore.commands.chat.chatroom;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class ChatRoomCMD extends AbstractCommandCollection {

    public ChatRoomCMD() {
        super("chatroom", "Manage chat rooms");
        addAliases("cr");
        requirePermission("fancycore.commands.chatroom");

        addSubCommand(new ChatRoomInfoCMD());
        addSubCommand(new ChatRoomListCMD());
        addSubCommand(new ChatRoomCreateCMD());
        addSubCommand(new ChatRoomDeleteCMD());
        addSubCommand(new ChatRoomClearChatCMD());
        addSubCommand(new ChatRoomMuteCMD());
        addSubCommand(new ChatRoomUnmuteCMD());
        addSubCommand(new ChatRoomCooldownCMD());

        addSubCommand(new ChatRoomWatchingCMD());
        addSubCommand(new ChatRoomWatchCMD());
        addSubCommand(new ChatRoomUnwatchCMD());
        addSubCommand(new ChatRoomSwitchCMD());
    }

}
