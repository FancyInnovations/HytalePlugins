package com.fancyinnovations.fancycore.permissions;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;

import java.util.List;
import java.util.UUID;

public class GroupImpl implements Group {

    private final String name;
    private String parent;
    private String prefix;
    private String suffix;
    private List<Permission> permissions;
    private List<UUID> members;

    public GroupImpl(
            String name,
            String parent,
            String prefix,
            String suffix,
            List<Permission> permissions,
            List<UUID> members) {
        this.name = name;
        this.parent = parent;
        this.prefix = prefix;
        this.suffix = suffix;
        this.permissions = permissions;
        this.members = members;
    }

    @Override
    public boolean checkPermission(String permission) {
        for (Permission p : permissions) {
            if (p.getPermission().equalsIgnoreCase(permission)) {
                return p.isEnabled();
            }
        }

        if (parent != null) {
            Group group = FancyCore.get().getPermissionService().getGroup(parent);
            if (group != null) {
                return group.checkPermission(permission);
            }
        }

        return false; // permission not found
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public void setPermission(String permission, boolean enabled) {
        for (Permission p : permissions) {
            if (p.getPermission().equalsIgnoreCase(permission)) {
                p.setEnabled(enabled);
                return;
            }
        }
        // If permission not found, add a new one
        permissions.add(new PermissionImpl(permission, enabled));
    }

    @Override
    public List<UUID> getMembers() {
        return members;
    }

    @Override
    public void setMembers(List<UUID> members) {
        this.members = members;
    }
}
