package com.fancyinnovations.fancycore.api.player;

import com.fancyinnovations.fancycore.api.permissions.Permission;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public interface FancyPlayer {

    UUID getUUID();
    String getUsername();

    List<Permission> getPermissions();
    void setPermissions(List<Permission> permissions);
    void setPermission(String permission, boolean enabled);
    void removePermission(String permission);
    boolean checkPermission(String permission);

    List<UUID> getGroups();
    void setGroups(List<UUID> groups);
    void addGroup(UUID group);
    void removeGroup(UUID group);
    boolean isInGroup(UUID group);

    String getNickname();
    void setNickname(String nickname);

    Color getChatColor();
    void setChatColor(Color chatColor);

    double getBalance();
    void setBalance(double balance);
    void addBalance(double balance);
    void removeBalance(double balance);

    long getFirstLoginTime();

    long getPlayTime();
    void addPlayTime(long playTime);

    boolean isDirty();
    void setDirty(boolean dirty);


    boolean isOnline();

    void sendMessage(String message);

}
