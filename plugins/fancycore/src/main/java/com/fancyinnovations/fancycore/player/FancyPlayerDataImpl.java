package com.fancyinnovations.fancycore.player;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import com.fancyinnovations.fancycore.permissions.PermissionImpl;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FancyPlayerDataImpl implements FancyPlayerData {

    private final List<Permission> permissions;
    private final List<UUID> groups;
    private final Map<String, Object> customData;
    private UUID uuid;
    private String username;
    private String nickname;
    private Color chatColor;
    private Map<Currency, Double> balances;
    private long firstLoginTime; // timestamp
    private long playTime; // in milliseconds
    private boolean isDirty;

    /**
     * Constructor for a new FancyPlayer
     */
    public FancyPlayerDataImpl(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.permissions = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.nickname = username; // default nickname is the username
        this.chatColor = Color.WHITE;
        this.balances = new ConcurrentHashMap<>();
        this.firstLoginTime = System.currentTimeMillis();
        this.playTime = 0L;
        this.customData = new ConcurrentHashMap<>();

        this.isDirty = true;
    }

    public FancyPlayerDataImpl(
            UUID uuid,
            String username,
            List<Permission> permissions,
            List<UUID> groups,
            String nickname,
            Color chatColor,
            Map<Currency, Double> balances,
            long firstLoginTime,
            long playTime,
            Map<String, Object> customData
    ) {
        this.uuid = uuid;
        this.username = username;
        this.permissions = permissions;
        this.groups = groups;
        this.nickname = nickname;
        this.chatColor = chatColor;
        this.balances = balances;
        this.firstLoginTime = firstLoginTime;
        this.playTime = playTime;
        this.customData = customData;
        this.isDirty = false;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        this.isDirty = true;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
        this.isDirty = true;
    }

    @Override
    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(List<Permission> permissions) {
        this.permissions.clear();
        this.permissions.addAll(permissions);
        this.isDirty = true;
    }

    @Override
    public void setPermission(String permission, boolean enabled) {
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
        this.permissions.removeIf(p -> p.getPermission().equals(permission));
    }

    @Override
    public List<UUID> getGroups() {
        return groups;
    }

    @Override
    public void setGroups(List<UUID> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }

    @Override
    public void addGroup(UUID group) {
        if (!this.groups.contains(group)) {
            this.groups.add(group);
            this.isDirty = true;
        }
    }

    @Override
    public void removeGroup(UUID group) {
        if (this.groups.remove(group)) {
            this.isDirty = true;
        }
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.isDirty = true;
    }

    @Override
    public Color getChatColor() {
        return chatColor;
    }

    @Override
    public void setChatColor(Color chatColor) {
        this.chatColor = chatColor;
        this.isDirty = true;
    }

    @Override
    public double getBalance(Currency currency) {
        return balances.getOrDefault(currency, 0.0);
    }

    @Override
    public Map<Currency, Double> getBalances() {
        return balances;
    }

    @Override
    public void setBalance(Currency currency, double balance) {
        balances.put(currency, balance);
        this.isDirty = true;
    }

    @Override
    public void addBalance(Currency currency, double balance) {
        double currentBalance = getBalance(currency);
        setBalance(currency, currentBalance + balance);
    }

    @Override
    public void removeBalance(Currency currency, double balance) {
        double currentBalance = getBalance(currency);
        setBalance(currency, currentBalance - balance);
    }

    @Override
    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    @ApiStatus.Internal
    public void setFirstLoginTime(long firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
        this.isDirty = true;
    }

    @Override
    public long getPlayTime() {
        return playTime;
    }

    @ApiStatus.Internal
    public void setPlayTime(long playTime) {
        this.playTime = playTime;
        this.isDirty = true;
    }

    @Override
    public void addPlayTime(long additionalTime) {
        setPlayTime(this.playTime + additionalTime);
    }

    @Override
    public Map<String, Object> getCustomData() {
        return customData;
    }

    @Override
    public <T> void setCustomData(String key, T value) {
        customData.put(key, value);
    }

    @Override
    public <T> T getCustomData(String key) {
        return (T) customData.get(key);
    }

    @Override
    public void removeCustomData(String key) {
        customData.remove(key);
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

}
