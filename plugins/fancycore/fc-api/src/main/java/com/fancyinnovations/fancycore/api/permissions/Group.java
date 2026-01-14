package com.fancyinnovations.fancycore.api.permissions;

import java.util.List;
import java.util.UUID;

public interface Group {

    String getName();

    List<String> getParents();

    void addParent(String parent);

    void removeParent(String parent);

    void clearParents();

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    List<Permission> getPermissions();

    void setPermissions(List<Permission> permissions);

    void setPermission(String permission, boolean enabled);

    void removePermission(String permission);

    List<UUID> getMembers();

    void addMember(UUID memberUUID);

    void removeMember(UUID memberUUID);

    void clearMembers();

    boolean checkPermission(String permission);
}
