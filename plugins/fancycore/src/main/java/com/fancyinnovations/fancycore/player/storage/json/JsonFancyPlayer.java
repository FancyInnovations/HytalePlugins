package com.fancyinnovations.fancycore.player.storage.json;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import com.fancyinnovations.fancycore.player.FancyPlayerDataImpl;
import com.google.gson.annotations.SerializedName;

import java.awt.*;
import java.util.*;
import java.util.List;

public record JsonFancyPlayer(
        String uuid,
        String username,
        List<JsonPermission> permissions,
        @SerializedName("group_ids") List<String> groupIDs,
        String nickname,
        @SerializedName("chat_color") String chatColor,
        Map<String, Double> balances,
        @SerializedName("first_login_time") long firstLoginTime,
        @SerializedName("play_time") long playTime,
        @SerializedName("custom_data") Map<String, Object> customData
) {

    /**
     * Converts a FancyPlayerImpl to a JsonFancyPlayer
     */
    public static JsonFancyPlayer from(FancyPlayerData player) {
        List<JsonPermission> permissions = new ArrayList<>();
        for (var perm : player.getPermissions()) {
            permissions.add(JsonPermission.from(perm));
        }

        List<String> groupIDs = new ArrayList<>();
        for (UUID groupID : player.getGroups()) {
            groupIDs.add(groupID.toString());
        }

        Map<String, Double> balances = new HashMap<>();
        for (var entry : player.getBalances().entrySet()) {
            balances.put(entry.getKey().name(), entry.getValue());
        }

        return new JsonFancyPlayer(
                player.getUUID().toString(),
                player.getUsername(),
                permissions,
                groupIDs,
                player.getNickname(),
                Integer.toHexString(player.getChatColor().getRGB()),
                balances,
                player.getFirstLoginTime(),
                player.getPlayTime(),
                player.getCustomData()
        );
    }

    /**
     * Converts this JsonFancyPlayer to a FancyPlayerImpl
     */
    public FancyPlayerData toFancyPlayer() {
        List<Permission> perms = new ArrayList<>();
        for (JsonPermission jsonPerm : permissions) {
            perms.add(jsonPerm.toPermission());
        }

        List<UUID> groups = new ArrayList<>();
        for (String groupID : groupIDs) {
            groups.add(UUID.fromString(groupID));
        }

        Map<Currency, Double> balances = new HashMap<>();
        for (var entry : this.balances.entrySet()) {
            Currency currency = FancyCore.get().getCurrencyService().getCurrency(entry.getKey());
            if (currency == null) {
                FancyCore.get().getFancyLogger().warn("Currency with name '" + entry.getKey() + "' not found for player " + username);
                continue;
            }

            balances.put(currency, entry.getValue());
        }

        return new FancyPlayerDataImpl(
                UUID.fromString(uuid),
                username,
                perms,
                groups,
                nickname,
                new Color((int) Long.parseLong(chatColor, 16), true),
                balances,
                firstLoginTime,
                playTime,
                customData
        );
    }

}
