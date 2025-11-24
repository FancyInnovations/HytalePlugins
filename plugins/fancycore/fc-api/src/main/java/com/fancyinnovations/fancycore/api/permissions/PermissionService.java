package com.fancyinnovations.fancycore.api.permissions;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    List<Group> getGroups();
    Group getGroup(String name);
    void addGroup(Group group);
    void removeGroup(String name);

    boolean hasPermission(FancyPlayer player, String permission);
    boolean hasPermission(UUID uuid, String permission);
    boolean hasPermission(String username, String permission);
}
