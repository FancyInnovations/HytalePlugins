package com.fancyinnovations.fancycore.permissions.storage.json;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.permissions.GroupImpl;

import java.util.*;

public record JsonGroup (
        String name,
        List<String> parents,
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
                group.getParents(),
                group.getPrefix(),
                group.getSuffix(),
                jsonPermissions,
                memberStrings
        );
    }

    public Group toGroup() {
        Set<String> parentsSet = new HashSet<>();
        if (parents != null) {
            parentsSet.addAll(parents);
        }

        List<Permission> perms = new ArrayList<>();
        if (permissions != null) {
            for (JsonPermission jsonPerm : permissions) {
                perms.add(jsonPerm.toPermission());
            }
        }

        Set<UUID> memberUUIDs = new HashSet<>();
        if (members != null) {
            for (String member : members) {
                memberUUIDs.add(UUID.fromString(member));
            }
        }

        return new GroupImpl(
                this.name,
                parentsSet,
                this.prefix,
                this.suffix,
                perms,
                memberUUIDs
        );
    }

}
