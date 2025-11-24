package com.fancyinnovations.fancycore.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.moderation.PunishmentType;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FakeHytalePlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FancyPlayerImpl implements FancyPlayer {

    private final FancyPlayerData data;
    private FakeHytalePlayer player;

    public FancyPlayerImpl(FancyPlayerData data) {
        this.data = data;
        this.player = null;
    }

    public FancyPlayerImpl(FancyPlayerData data, FakeHytalePlayer player) {
        this.data = data;
        this.player = player;

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

    @Override
    public boolean checkPermission(String permission) {
        for (Permission p : data.getPermissions()) {
            if (p.getPermission().equalsIgnoreCase(permission)) {
                return p.isEnabled();
            }
        }

        for (UUID groupID : data.getGroups()) {
            // TODO: Fetch group by ID and check its permissions
//            if (g.checkPermission(permission)) {
//                return true;
//            }
        }

        return false; // permission not found
    }

    @Override
    public boolean isInGroup(UUID group) {
        for (UUID groupID : data.getGroups()) {
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
