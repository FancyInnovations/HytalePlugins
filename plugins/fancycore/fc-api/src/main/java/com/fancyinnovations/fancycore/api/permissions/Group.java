package com.fancyinnovations.fancycore.api.permissions;

import java.util.List;
import java.util.UUID;

public interface Group {

    String getName();

    int getWeight();

    void setWeight(int weight);

    List<String> getParents();

    void addParent(String parent);

    void removeParent(String parent);

    void clearParents();

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    /**
     * Gets the permissions directly assigned to this group.
     *
     * @return List of permissions
     */
    List<Permission> getPermissions();

    void setPermissions(List<Permission> permissions);

    /**
     * Gets all permissions including inherited ones from parent groups.
     *
     * @return List of all permissions
     */
    List<Permission> getAllPermissions();

    void setPermission(String permission, boolean enabled);

    void removePermission(String permission);

    List<UUID> getMembers();

    void addMember(UUID memberUUID);

    void removeMember(UUID memberUUID);

    void clearMembers();

    boolean checkPermission(String permission);
}
