package com.fancyinnovations.fancycore.player.storage.json;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import com.fancyinnovations.fancycore.api.player.Home;
import com.fancyinnovations.fancycore.player.FancyPlayerDataImpl;
import com.google.gson.annotations.SerializedName;

import java.util.*;

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
        @SerializedName("last_login_time") long lastLoginTime,
        @SerializedName("is_vanished") boolean isVanished,
        @SerializedName("is_flying") boolean isFlying,
        @SerializedName("play_time") long playTime,
        List<Home> homes,
        @SerializedName("kit_cooldowns") Map<String, Long> kitCooldowns,
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
        for (var entry : player.getBalances().entrySet()) {
            balances.put(entry.getKey().name(), entry.getValue());
        }

        return new JsonFancyPlayer(
                player.getUUID().toString(),
                player.getUsername(),
                permissions,
                player.getGroups(),
                player.getNickname(),
                player.getChatColor(),
                ignoredPlayers,
                player.isPrivateMessagesEnabled(),
                balances,
                player.getFirstLoginTime(),
                player.getLastLoginTime(),
                player.isVanished(),
                player.isFlying(),
                player.getPlayTime(),
                player.getHomes(),
                player.getKitCooldowns(),
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

        // For backwards compatibility: if lastLoginTime is 0 or not set, use firstLoginTime
        long actualLastLoginTime = (lastLoginTime > 0) ? lastLoginTime : firstLoginTime;
        
        return new FancyPlayerDataImpl(
                UUID.fromString(uuid),
                username,
                perms,
                groups,
                nickname,
                chatColor,
                ignoredPlayerUUIDs,
                privateMessagesEnabled,
                balances,
                firstLoginTime,
                actualLastLoginTime,
                isVanished,
                isFlying,
                playTime,
                homes,
                kitCooldowns,
                customData
        );
    }

}
