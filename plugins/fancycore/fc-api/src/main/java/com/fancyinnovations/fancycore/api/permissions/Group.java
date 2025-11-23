package com.fancyinnovations.fancycore.api.permissions;

import java.util.List;
import java.util.UUID;

public interface Group {

    UUID getId();

    String getName();

    UUID getParentId();
    void setParentId(UUID parentId);

    String getPrefix();
    void setPrefix(String prefix);

    String getSuffix();
    void setSuffix(String suffix);

    List<Permission> getPermissions();
    void setPermissions(List<Permission> permissions);

    List<UUID> getMembers();
    void setMembers(List<UUID> members);

    boolean checkPermission(String permission);
}
