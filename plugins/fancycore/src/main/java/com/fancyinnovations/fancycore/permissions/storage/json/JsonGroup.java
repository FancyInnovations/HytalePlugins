package com.fancyinnovations.fancycore.permissions.storage.json;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.permissions.GroupImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record JsonGroup (
        String name,
        String parent,
        String prefix,
        String suffix,
        List<JsonPermission> permissions,
        List<String> members
){

    public static JsonGroup from(Group group) {
        List<JsonPermission> jsonPermissions = group.getPermissions().stream()
                .map(JsonPermission::from)
                .toList();

        List<String> memberStrings = group.getMembers().stream()
                .map(UUID::toString)
                .toList();

        return new JsonGroup(
                group.getName(),
                group.getParent(),
                group.getPrefix(),
                group.getSuffix(),
                jsonPermissions,
                memberStrings
        );
    }

    public Group toGroup() {
        List<Permission> perms = new ArrayList<>();
        if (permissions != null) {
            for (JsonPermission jsonPerm : permissions) {
                perms.add(jsonPerm.toPermission());
            }
        }

        List<UUID> memberUUIDs = new ArrayList<>();
        if (members != null) {
            for (String member : members) {
                memberUUIDs.add(UUID.fromString(member));
            }
        }

        return new GroupImpl(
                this.name,
                this.parent,
                this.prefix,
                this.suffix,
                perms,
                memberUUIDs
        );
    }

}
