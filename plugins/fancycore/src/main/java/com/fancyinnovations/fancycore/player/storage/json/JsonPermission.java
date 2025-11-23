package com.fancyinnovations.fancycore.player.storage.json;

import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.permissions.PermissionImpl;

public record JsonPermission(
        String permission,
        boolean enabled
) {

    public static JsonPermission from(Permission permission) {
        return new JsonPermission(permission.getPermission(), permission.isEnabled());
    }

    public Permission toPermission() {
        return new PermissionImpl(permission, enabled);
    }
}
