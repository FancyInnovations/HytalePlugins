package com.fancyinnovations.fancycore.permissions;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GroupImpl implements Group {

    private final String name;
    private final Set<String> parents;
    private final Set<UUID> members;
    private String prefix;
    private String suffix;
    private List<Permission> permissions;

    public GroupImpl(
            String name,
            Set<String> parents,
            String prefix,
            String suffix,
            List<Permission> permissions,
            Set<UUID> members) {
        this.name = name;
        this.parents = parents;
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

        for (String parent : parents) {
            Group group = FancyCore.get().getPermissionService().getGroup(parent);
            if (group != null) {
                // TODO return false if explicitly denied in parent group
                if (group.checkPermission(permission)) {
                    return true;
                }
            }
        }

        return false; // permission not found
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getParents() {
        return List.of();
    }

    @Override
    public void addParent(String parent) {
        this.parents.add(parent);
    }

    @Override
    public void removeParent(String parent) {
        this.parents.remove(parent);
    }

    @Override
    public void clearParents() {
        this.parents.clear();
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
    public void removePermission(String permission) {
        this.permissions.removeIf(perm -> perm.getPermission().equalsIgnoreCase(permission));
    }

    @Override
    public List<UUID> getMembers() {
        return List.copyOf(members);
    }

    @Override
    public void addMember(UUID memberUUID) {
        this.members.add(memberUUID);
    }

    @Override
    public void removeMember(UUID memberUUID) {
        this.members.remove(memberUUID);
    }

    @Override
    public void clearMembers() {
        this.members.clear();
    }

}
