package com.fancyinnovations.fancycore.permissions;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;

import java.util.*;

public class GroupImpl implements Group {

    private final String name;
    private final Set<String> parents;
    private final Set<UUID> members;
    private int weight;
    private String prefix;
    private String suffix;
    private List<Permission> permissions;
    private Map<String, Object> metadata;

    public GroupImpl(
            String name,
            int weight,
            Set<String> parents,
            String prefix,
            String suffix,
            List<Permission> permissions,
            Map<String, Object> metadata,
            Set<UUID> members
    ) {
        this.name = name;
        this.weight = weight;
        this.parents = new HashSet<>(parents);
        this.prefix = prefix;
        this.suffix = suffix;
        this.permissions = new ArrayList<>(permissions);
        this.metadata = new HashMap<>(metadata);
        this.members = new HashSet<>(members);
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
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public List<String> getParents() {
        return List.copyOf(parents);
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
    public List<Permission> getAllPermissions() {
        Set<String> seenPermissions = new HashSet<>();
        List<Permission> allPermissions = new ArrayList<>(permissions);

        for (String parentName : parents) {
            Group parentGroup = FancyCore.get().getPermissionService().getGroup(parentName);
            if (parentGroup != null) {
                for (Permission perm : parentGroup.getAllPermissions()) {
                    if (!seenPermissions.contains(perm.getPermission())) {
                        allPermissions.add(perm);
                        seenPermissions.add(perm.getPermission());
                    }
                }
            }
        }

        return allPermissions;
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
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public Object getMetadataValue(String key) {
        return this.metadata.get(key);
    }

    @Override
    public Object getMetadataValueInherited(String key) {
        Object value = this.metadata.get(key);
        if (value != null) {
            return value;
        }

        for (String parentName : parents) {
            Group parentGroup = FancyCore.get().getPermissionService().getGroup(parentName);
            if (parentGroup != null) {
                value = parentGroup.getMetadataValueInherited(key);
                if (value != null) {
                    return value;
                }
            }
        }

        return null; // not found
    }

    @Override
    public void setMetadataValue(String key, Object value) {
        this.metadata.put(key, value);
    }

    @Override
    public void removeMetadataValue(String key) {
        this.metadata.remove(key);
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
