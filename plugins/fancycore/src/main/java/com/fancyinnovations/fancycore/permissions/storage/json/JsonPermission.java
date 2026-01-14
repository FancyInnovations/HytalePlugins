package com.fancyinnovations.fancycore.permissions.storage.json;

import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.permissions.PermissionImpl;

public record JsonPermission(
        String permission,
        boolean enabled
) {

    public static JsonPermission from(Permission perm) {
        return new JsonPermission(
                perm.getPermission(),
                perm.isEnabled()
        );
    }

    public Permission toPermission() {
        return new PermissionImpl(
                this.permission,
                this.enabled
        );
    }

}
