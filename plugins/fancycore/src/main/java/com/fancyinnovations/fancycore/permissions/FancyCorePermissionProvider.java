package com.fancyinnovations.fancycore.permissions;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.permissions.provider.PermissionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FancyCorePermissionProvider implements PermissionProvider {

    @Override
    public @NotNull String getName() {
        return "FancyCore-Permissions";
    }

    @Override
    public void addUserPermissions(@NotNull UUID uuid, @NotNull Set<String> permissions) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(uuid);
        if (fp == null) {
            return;
        }

        for (String permission : permissions) {
            fp.getData().setPermission(permission, true);
        }
    }

    @Override
    public void removeUserPermissions(@NotNull UUID uuid, @NotNull Set<String> permissions) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(uuid);
        if (fp == null) {
            return;
        }

        for (String permission : permissions) {
            fp.getData().removePermission(permission);
        }
    }

    @Override
    public Set<String> getUserPermissions(@NotNull UUID uuid) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(uuid);
        if (fp == null) {
            return Set.of();
        }

        Set<String> userPermissions = new HashSet<>();
        for (Permission permission : fp.getData().getPermissions()) {
            if (permission.isEnabled()) {
                userPermissions.add(permission.getPermission());
            }
        }

        for (String group : fp.getData().getGroups()) {
            Group g = PermissionService.get().getGroup(group);
            if (g == null) {
                continue;
            }

            for (Permission permission : g.getAllPermissions()) {
                if (permission.isEnabled()) {
                    userPermissions.add(permission.getPermission());
                }
            }
        }

        return userPermissions;
    }

    @Override
    public void addGroupPermissions(@NotNull String groupName, @NotNull Set<String> permissions) {
        Group group = PermissionService.get().getGroup(groupName);
        if (group == null) {
            return;
        }

        for (String permission : permissions) {
            group.setPermission(permission, true);
        }
    }

    @Override
    public void removeGroupPermissions(@NotNull String groupName, @NotNull Set<String> permissions) {
        Group group = PermissionService.get().getGroup(groupName);
        if (group == null) {
            return;
        }

        for (String permission : permissions) {
            group.removePermission(permission);
        }
    }

    @Override
    public Set<String> getGroupPermissions(@NotNull String groupName) {
        Group group = PermissionService.get().getGroup(groupName);
        if (group == null) {
            return Set.of();
        }

        Set<String> groupPermissions = new HashSet<>();
        for (Permission permission : group.getAllPermissions()) {
            if (permission.isEnabled()) {
                groupPermissions.add(permission.getPermission());
            }
        }

        return groupPermissions;
    }

    @Override
    public void addUserToGroup(@NotNull UUID uuid, @NotNull String groupName) {
        Group group = PermissionService.get().getGroup(groupName);
        if (group == null) {
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(uuid);
        if (fp == null) {
            return;
        }

        group.addMember(uuid);
        fp.getData().addGroup(group.getName());

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);
    }

    @Override
    public void removeUserFromGroup(@NotNull UUID uuid, @NotNull String groupName) {
        Group group = PermissionService.get().getGroup(groupName);
        if (group == null) {
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(uuid);
        if (fp == null) {
            return;
        }

        group.removeMember(uuid);
        fp.getData().removeGroup(group.getName());

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);
    }

    @Override
    public Set<String> getGroupsForUser(@NotNull UUID uuid) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(uuid);
        if (fp == null) {
            return Set.of();
        }

        return Set.copyOf(fp.getData().getGroups());
    }
}
