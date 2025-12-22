package com.fancyinnovations.fancycore.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.moderation.PunishmentType;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FakeHytalePlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import org.jetbrains.annotations.Nullable;

public class FancyPlayerImpl implements FancyPlayer {

    private final FancyPlayerData data;
    private FakeHytalePlayer player;
    private long joinedAt;

    public FancyPlayerImpl(FancyPlayerData data) {
        this.data = data;
        this.player = null;
    }

    public FancyPlayerImpl(FancyPlayerData data, FakeHytalePlayer player) {
        this.data = data;
        this.player = player;
        this.joinedAt = -1;

        this.data.setUUID(player.getUUID());
        this.data.setUsername(data.getUsername());
    }

    @Override
    public FancyPlayerData getData() {
        return null;
    }

    @Override
    public @Nullable FakeHytalePlayer getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(FakeHytalePlayer player) {
        this.player = player;

        if (player == null) {
            return;
        }

        this.data.setUUID(player.getUUID());
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
        return player != null;
    }

    @Override
    public void sendMessage(String message) {
        // TODO: Implement message sending logic
    }
}
