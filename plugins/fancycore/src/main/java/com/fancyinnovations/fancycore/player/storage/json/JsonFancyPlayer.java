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
        @SerializedName("groups") List<String> groups,
        String nickname,
        @SerializedName("chat_color") String chatColor,
        @SerializedName("ignored_players") List<String> ignoredPlayers,
        @SerializedName("private_messages_enabled") boolean privateMessagesEnabled,
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

        List<String> ignoredPlayers = player.getIgnoredPlayers().stream()
                .map(UUID::toString)
                .toList();

        Map<String, Double> balances = new HashMap<>();
        //hytale.system.command.help
        for (var entry : player.getBalances().entrySet()) {
            balances.put(entry.getKey().name(), entry.getValue());
        }

        return new JsonFancyPlayer(
                player.getUUID().toString(),
                player.getUsername(),
                permissions,
                player.getGroups(),
                player.getNickname(),
                Integer.toHexString(player.getChatColor().getRGB()),
                ignoredPlayers,
                player.isPrivateMessagesEnabled(),
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
        if (permissions != null) {
            for (JsonPermission jsonPerm : permissions) {
                perms.add(jsonPerm.toPermission());
            }
        }

        List<UUID> ignoredPlayerUUIDs = new ArrayList<>();
        if (ignoredPlayers != null) {
            for (String ignoredPlayer : ignoredPlayers) {
                try {
                    ignoredPlayerUUIDs.add(UUID.fromString(ignoredPlayer));
                } catch (IllegalArgumentException e) {
                    FancyCore.get().getFancyLogger().warn("Invalid UUID '" + ignoredPlayer + "' in ignored players for player " + username);
                }
            }
        }

        Map<Currency, Double> balances = new HashMap<>();
        if (this.balances != null) {
            for (var entry : this.balances.entrySet()) {
                Currency currency = FancyCore.get().getCurrencyService().getCurrency(entry.getKey());
                if (currency == null) {
                    FancyCore.get().getFancyLogger().warn("Currency with name '" + entry.getKey() + "' not found for player " + username);
                    continue;
                }

                balances.put(currency, entry.getValue());
            }
        }

        return new FancyPlayerDataImpl(
                UUID.fromString(uuid),
                username,
                perms,
                groups,
                nickname,
                new Color((int) Long.parseLong(chatColor, 16), true),
                ignoredPlayerUUIDs,
                privateMessagesEnabled,
                balances,
                firstLoginTime,
                playTime,
                customData
        );
    }

}
