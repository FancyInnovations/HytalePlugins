package com.fancyinnovations.fancycore.permissions;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;

import java.util.List;
import java.util.UUID;

public class GroupImpl implements Group {

    private final UUID id;
    private final String name;
    private UUID parentId;
    private String prefix;
    private String suffix;
    private List<Permission> permissions;
    private List<UUID> members;

    public GroupImpl(
            UUID id,
            String name,
            UUID parentId,
            String prefix,
            String suffix,
            List<Permission> permissions,
            List<UUID> members) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
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

        if (parentId != null) {
            // TODO: Recursively check parent group permissions
        }

        return false; // permission not found
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(UUID parentId) {
        this.parentId = parentId;
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
    public List<UUID> getMembers() {
        return members;
    }

    @Override
    public void setMembers(List<UUID> members) {
        this.members = members;
    }
}
