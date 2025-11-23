package com.fancyinnovations.fancycore.player.storage.json;

import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.player.FancyPlayerImpl;
import com.google.gson.annotations.SerializedName;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record JsonFancyPlayer(
        String uuid,
        String username,
        List<JsonPermission> permissions,
        @SerializedName("group_ids") List<String> groupIDs,
        String nickname,
        @SerializedName("chat_color") String chatColor,
        double balance,
        @SerializedName("first_login_time") long firstLoginTime,
        @SerializedName("play_time") long playTime
) {

    /**
     * Converts a FancyPlayerImpl to a JsonFancyPlayer
     */
    public static JsonFancyPlayer from(FancyPlayerImpl player) {
        List<JsonPermission> permissions = new ArrayList<>();
        for (var perm : player.getPermissions()) {
            permissions.add(JsonPermission.from(perm));
        }

        List<String> groupIDs = new ArrayList<>();
        for (UUID groupID : player.getGroups()) {
            groupIDs.add(groupID.toString());
        }

        return new JsonFancyPlayer(
                player.getUUID().toString(),
                player.getUsername(),
                permissions,
                groupIDs,
                player.getNickname(),
                Integer.toHexString(player.getChatColor().getRGB()),
                player.getBalance(),
                player.getFirstLoginTime(),
                player.getPlayTime()
        );
    }

    /**
     * Converts this JsonFancyPlayer to a FancyPlayerImpl
     */
    public FancyPlayerImpl toFancyPlayer() {
        List<Permission> perms = new ArrayList<>();
        for (JsonPermission jsonPerm : permissions) {
            perms.add(jsonPerm.toPermission());
        }

        List<UUID> groups = new ArrayList<>();
        for (String groupID : groupIDs) {
            groups.add(UUID.fromString(groupID));
        }

        return new FancyPlayerImpl(
                UUID.fromString(uuid),
                username,
                perms,
                groups,
                nickname,
                new Color((int) Long.parseLong(chatColor, 16), true),
                balance,
                firstLoginTime,
                playTime
        );
    }

}
