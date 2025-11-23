package com.fancyinnovations.fancycore.player;

import com.fancyinnovations.fancycore.api.events.player.PlayerModifiedEvent;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.permissions.PermissionImpl;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FancyPlayerImpl implements FancyPlayer {

    private final UUID uuid;
    private final String username;
    private final List<Permission> permissions;
    private final List<UUID> groups;
    private String nickname;
    private Color chatColor;
    private double balance;
    private long firstLoginTime; // timestamp
    private long playTime; // in milliseconds

    private boolean isDirty;

    /**
     * Constructor for a new FancyPlayer
     */
    public FancyPlayerImpl(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.permissions = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.nickname = username; // default nickname is the username
        this.chatColor = Color.WHITE;
        this.balance = 0.0;
        this.firstLoginTime = System.currentTimeMillis();
        this.playTime = 0L;
        this.isDirty = true;
    }

    public FancyPlayerImpl(
        UUID uuid,
        String username,
        List<Permission> permissions,
          List<UUID> groups,
        String nickname,
        Color chatColor,
        double balance,
        long firstLoginTime,
        long playTime
    ) {
        this.uuid = uuid;
        this.username = username;
        this.permissions = permissions;
        this.groups = groups;
        this.nickname = nickname;
        this.chatColor = chatColor;
        this.balance = balance;
        this.firstLoginTime = firstLoginTime;
        this.playTime = playTime;
        this.isDirty = false;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(List<Permission> permissions) {
        PlayerModifiedEvent playerModifiedEvent = new PlayerModifiedEvent(this, PlayerModifiedEvent.ModifiedField.PERMISSIONS, this.permissions, permissions);
        if (!playerModifiedEvent.fire()) {
            return;
        }

        this.permissions.clear();
        this.permissions.addAll((List<Permission>) playerModifiedEvent.getNewData());
        this.isDirty = true;
    }

    @Override
    public void setPermission(String permission, boolean enabled) {
        PlayerModifiedEvent playerModifiedEvent = new PlayerModifiedEvent(this, PlayerModifiedEvent.ModifiedField.PERMISSIONS, this.permissions, permissions);
        if (!playerModifiedEvent.fire()) {
            return;
        }

        for (Permission p : this.permissions) {
            if (p.getPermission().equals(permission)) {
                p.setEnabled(enabled);
                return;
            }
        }

        this.permissions.add(new PermissionImpl(permission, enabled));
    }

    @Override
    public void removePermission(String permission) {
        PlayerModifiedEvent playerModifiedEvent = new PlayerModifiedEvent(this, PlayerModifiedEvent.ModifiedField.PERMISSIONS, this.permissions, permissions);
        if (!playerModifiedEvent.fire()) {
            return;
        }

        this.permissions.removeIf(p -> p.getPermission().equals(permission));
    }

    @Override
    public boolean checkPermission(String permission) {
        for (Permission p : permissions) {
            if (p.getPermission().equalsIgnoreCase(permission)) {
                return p.isEnabled();
            }
        }

        for (UUID groupID : groups) {
            // TODO: Fetch group by ID and check its permissions
//            if (g.checkPermission(permission)) {
//                return true;
//            }
        }

        return false; // permission not found
    }

    @Override
    public List<UUID> getGroups() {
        return List.of();
    }

    @Override
    public void setGroups(List<UUID> groups) {

    }

    @Override
    public void addGroup(UUID group) {

    }

    @Override
    public void removeGroup(UUID group) {

    }

    @Override
    public boolean isInGroup(UUID group) {
        return false;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        PlayerModifiedEvent playerModifiedEvent = new PlayerModifiedEvent(this, PlayerModifiedEvent.ModifiedField.NICKNAME, this.nickname, nickname);
        if (!playerModifiedEvent.fire()) {
            return;
        }

        this.nickname = playerModifiedEvent.getNewData().toString();
        this.isDirty = true;
    }

    @Override
    public Color getChatColor() {
        return chatColor;
    }

    @Override
    public void setChatColor(Color chatColor) {
        PlayerModifiedEvent playerModifiedEvent = new PlayerModifiedEvent(this, PlayerModifiedEvent.ModifiedField.CHAT_COLOR, this.chatColor, chatColor);
        if (!playerModifiedEvent.fire()) {
            return;
        }

        this.chatColor = (Color) playerModifiedEvent.getNewData();
        this.isDirty = true;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double balance) {
        PlayerModifiedEvent playerModifiedEvent = new PlayerModifiedEvent(this, PlayerModifiedEvent.ModifiedField.BALANCE, this.balance, balance);
        if (!playerModifiedEvent.fire()) {
            return;
        }
        this.isDirty = true;
    }

    @Override
    public void addBalance(double amount) {
        setBalance(this.balance + amount);
    }

    @Override
    public void removeBalance(double amount) {
        setBalance(this.balance - amount);
    }

    @Override
    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    @ApiStatus.Internal
    public void setFirstLoginTime(long firstLoginTime) {
        PlayerModifiedEvent playerModifiedEvent = new PlayerModifiedEvent(this, PlayerModifiedEvent.ModifiedField.FIRST_LOGIN_TIME, this.firstLoginTime, firstLoginTime);
        if (!playerModifiedEvent.fire()) {
            return;
        }

        this.firstLoginTime = (long) playerModifiedEvent.getNewData();
        this.isDirty = true;
    }

    @Override
    public long getPlayTime() {
        return playTime;
    }

    @ApiStatus.Internal
    public void setPlayTime(long playTime) {
        PlayerModifiedEvent playerModifiedEvent = new PlayerModifiedEvent(this, PlayerModifiedEvent.ModifiedField.PLAY_TIME, this.playTime, playTime);
        if (!playerModifiedEvent.fire()) {
            return;
        }

        this.playTime = (long) playerModifiedEvent.getNewData();
        this.isDirty = true;
    }

    @Override
    public void addPlayTime(long additionalTime) {
        setPlayTime(this.playTime + additionalTime);
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    @Override
    public boolean isOnline() {
        // TODO: Implement online status check
        return true;
    }

    @Override
    public void sendMessage(String message) {
        // TODO: Implement message sending logic
    }
}
