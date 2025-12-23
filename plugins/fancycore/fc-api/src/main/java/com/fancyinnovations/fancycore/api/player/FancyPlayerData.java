package com.fancyinnovations.fancycore.api.player;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the data associated with a Fancy Player.
 */
public interface FancyPlayerData {

    UUID getUUID();

    @ApiStatus.Internal
    void setUUID(UUID uuid);

    String getUsername();

    @ApiStatus.Internal
    void setUsername(String username);

    List<Permission> getPermissions();

    void setPermissions(List<Permission> permissions);

    void setPermission(String permission, boolean enabled);

    void removePermission(String permission);

    List<String> getGroups();

    void setGroups(List<String> groups);

    void addGroup(String group);

    void removeGroup(String group);

    String getNickname();

    void setNickname(String nickname);

    Color getChatColor();

    void setChatColor(Color chatColor);

    double getBalance(Currency currency);

    Map<Currency, Double> getBalances();

    void setBalance(Currency currency, double balance);

    void addBalance(Currency currency, double balance);

    void removeBalance(Currency currency, double balance);

    long getFirstLoginTime();

    long getPlayTime();

    void addPlayTime(long playTime);

    Map<String, Object> getCustomData();

    <T> void setCustomData(String key, T value);

    <T> T getCustomData(String key);

    void removeCustomData(String key);

    boolean isDirty();

    void setDirty(boolean dirty);

    @ApiStatus.Internal
    String toJson();

}
