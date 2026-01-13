package com.fancyinnovations.fancycore.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.events.chat.PlayerSwitchedChatRoomEvent;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.moderation.PunishmentType;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.Nullable;

public class FancyPlayerImpl implements FancyPlayer {

    private final FancyPlayerData data;
    private PlayerRef player;
    private long joinedAt;
    private ChatRoom currentChatRoom;

    public FancyPlayerImpl(FancyPlayerData data) {
        this.data = data;
        this.player = null;
    }

    public FancyPlayerImpl(FancyPlayerData data, PlayerRef player) {
        this.data = data;
        this.player = player;
        this.joinedAt = -1;

        this.data.setUUID(player.getUuid());
        this.data.setUsername(data.getUsername());
    }

    @Override
    public FancyPlayerData getData() {
        return data;
    }

    @Override
    public @Nullable PlayerRef getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(PlayerRef player) {
        this.player = player;

        if (player == null) {
            return;
        }

        this.data.setUUID(player.getUuid());
        this.data.setUsername(data.getUsername());
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public boolean checkPermission(String permission) {
        for (Permission p : data.getPermissions()) {
            if (p.getPermission().equalsIgnoreCase(permission)) {
                return p.isEnabled();
            }
        }

        for (String groupID : data.getGroups()) {
            Group group = FancyCore.get().getPermissionService().getGroup(groupID);
            if (group == null) {
                continue;
            }

            if (group.checkPermission(permission)) {
                return true;
            }
        }

        return false; // permission not found
    }

    @Override
    public boolean isInGroup(String group) {
        for (String groupID : data.getGroups()) {
            if (groupID.equals(group)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Punishment isMuted() {
        for (Punishment punishment : FancyCore.get().getPunishmentService().getPunishmentsForPlayer(this)) {
            if (punishment.type() == PunishmentType.MUTE && punishment.isActive()) {
                return punishment;
            }
        }

        return null;
    }

    @Override
    public Punishment isBanned() {
        for (Punishment punishment : FancyCore.get().getPunishmentService().getPunishmentsForPlayer(this)) {
            if (punishment.type() == PunishmentType.BAN && punishment.isActive()) {
                return punishment;
            }
        }

        return null;
    }


    @Override
    public boolean isOnline() {
        // TODO (HTEA): implement a proper online check with real player object
        return player != null;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(Message.parse(message));
    }

    @Override
    public ChatRoom getCurrentChatRoom() {
        if (currentChatRoom == null) {
            String defaultChatroomName = FancyCore.get().getConfig().getDefaultChatroom();
            currentChatRoom = FancyCore.get().getChatService().getChatRoom(defaultChatroomName);
            if (currentChatRoom == null) {
                currentChatRoom = FancyCore.get().getChatService().createChatRoom(defaultChatroomName);
            }

            currentChatRoom.startWatching(this);
        }

        return currentChatRoom;
    }

    @Override
    public void switchChatRoom(ChatRoom room) {
        if (room.getName().equals(this.currentChatRoom)) {
            return;
        }

        if (!new PlayerSwitchedChatRoomEvent(this, this.currentChatRoom, room).fire()) {
            return;
        }

        this.currentChatRoom = room;
    }
}
