package com.fancyinnovations.fancycore.api.permissions;

import java.util.List;
import java.util.UUID;

public interface Group {

    String getName();

    String getParent();

    void setParent(String parent);

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    List<Permission> getPermissions();

    void setPermissions(List<Permission> permissions);

    void setPermission(String permission, boolean enabled);

    List<UUID> getMembers();

    void setMembers(List<UUID> members);

    boolean checkPermission(String permission);
}
