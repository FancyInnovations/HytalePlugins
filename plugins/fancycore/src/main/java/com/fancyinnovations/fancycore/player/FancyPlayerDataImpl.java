package com.fancyinnovations.fancycore.player;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import com.fancyinnovations.fancycore.api.player.Home;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.permissions.PermissionImpl;
import com.fancyinnovations.fancycore.player.storage.json.JsonFancyPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FancyPlayerDataImpl implements FancyPlayerData {

    private final List<Permission> permissions;
    private final List<String> groups;
    private final Map<String, Object> customData;
    private final Map<String, Home> homes;
    private final Map<String, Long> kitCooldowns;
    private UUID uuid;
    private String username;
    private String nickname;
    private String chatColor;
    private List<UUID> ignoredPlayers;
    private boolean enabledPrivateMessages;
    private Map<Currency, Double> balances;
    private long firstLoginTime; // timestamp
    private long lastLoginTime; // timestamp
    private boolean isVanished;
    private boolean isFlying;
    private long playTime; // in milliseconds
    private boolean isDirty;

    /**
     * Constructor for a new FancyPlayer
     */
    public FancyPlayerDataImpl(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.permissions = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.nickname = username; // default nickname is the username
        this.chatColor = "";
        this.ignoredPlayers = new ArrayList<>();
        this.enabledPrivateMessages = true;
        this.balances = new ConcurrentHashMap<>();
        this.firstLoginTime = System.currentTimeMillis();
        this.lastLoginTime = System.currentTimeMillis();
        this.isVanished = false;
        this.isFlying = false;
        this.playTime = 0L;
        this.homes = new ConcurrentHashMap<>();
        this.kitCooldowns = new ConcurrentHashMap<>();
        this.customData = new ConcurrentHashMap<>();

        this.isDirty = true;
    }

    public FancyPlayerDataImpl(
            UUID uuid,
            String username,
            List<Permission> permissions,
            List<String> groups,
            String nickname,
            String chatColor,
            List<UUID> ignoredPlayers,
            boolean enabledPrivateMessages,
            Map<Currency, Double> balances,
            long firstLoginTime,
            long lastLoginTime,
            boolean isVanished,
            boolean isFlying,
            long playTime,
            List<Home> homes,
            Map<String, Long> kitCooldowns,
            Map<String, Object> customData
    ) {
        this.uuid = uuid;
        this.username = username;
        this.permissions = permissions;
        this.groups = groups;
        this.nickname = nickname;
        this.chatColor = chatColor;
        this.ignoredPlayers = ignoredPlayers;
        this.enabledPrivateMessages = enabledPrivateMessages;
        this.balances = balances;
        this.firstLoginTime = firstLoginTime;
        this.lastLoginTime = lastLoginTime;
        this.isVanished = isVanished;
        this.isFlying = isFlying;
        this.playTime = playTime;
        this.customData = customData;
        this.homes = new ConcurrentHashMap<>();
        for (Home home : homes) {
            this.homes.put(home.name(), home);
        }
        this.kitCooldowns = new ConcurrentHashMap<>(kitCooldowns);
        this.isDirty = false;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        this.isDirty = true;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
        this.isDirty = true;
    }

    @Override
    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(List<Permission> permissions) {
        this.permissions.clear();
        this.permissions.addAll(permissions);
        this.isDirty = true;
    }

    @Override
    public void setPermission(String permission, boolean enabled) {
        for (Permission p : this.permissions) {
            if (p.getPermission().equals(permission)) {
                p.setEnabled(enabled);
                return;
            }
        }

        this.permissions.add(new PermissionImpl(permission, enabled));
    }

    @Override
    public void removePermission(String permission) {
        this.permissions.removeIf(p -> p.getPermission().equals(permission));
    }

    @Override
    public List<String> getGroups() {
        return groups;
    }

    @Override
    public void setGroups(List<String> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }

    @Override
    public List<Group> getGroupSortedByWeight() {
        List<Group> groupObjects = new ArrayList<>();
        for (String groupName : groups) {
            Group group = PermissionService.get().getGroup(groupName);
            if (group != null) {
                groupObjects.add(group);
            }
        }

        groupObjects.sort((g1, g2) -> Integer.compare(g2.getWeight(), g1.getWeight())); // Descending order
        return groupObjects;
    }

    @Override
    public void addGroup(String group) {
        if (!this.groups.contains(group)) {
            this.groups.add(group);
            this.isDirty = true;
        }
    }

    @Override
    public void removeGroup(String group) {
        if (this.groups.remove(group)) {
            this.isDirty = true;
        }
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.isDirty = true;
    }

    @Override
    public String getChatColor() {
        return chatColor;
    }

    @Override
    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
        this.isDirty = true;
    }

    @Override
    public List<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    @Override
    public void addIgnoredPlayer(UUID playerUUID) {
        if (!this.ignoredPlayers.contains(playerUUID)) {
            this.ignoredPlayers.add(playerUUID);
            this.isDirty = true;
        }
    }

    @Override
    public void removeIgnoredPlayer(UUID playerUUID) {
        if (this.ignoredPlayers.remove(playerUUID)) {
            this.isDirty = true;
        }
    }

    @Override
    public boolean isPrivateMessagesEnabled() {
        return enabledPrivateMessages;
    }

    @Override
    public void setPrivateMessagesEnabled(boolean enabled) {
        this.enabledPrivateMessages = enabled;
        this.isDirty = true;
    }

    @Override
    public double getBalance(Currency currency) {
        return balances.getOrDefault(currency, 0.0);
    }

    @Override
    public Map<Currency, Double> getBalances() {
        return balances;
    }

    @Override
    public void setBalance(Currency currency, double balance) {
        balances.put(currency, balance);
        this.isDirty = true;
    }

    @Override
    public void addBalance(Currency currency, double balance) {
        double currentBalance = getBalance(currency);
        setBalance(currency, currentBalance + balance);
    }

    @Override
    public void removeBalance(Currency currency, double balance) {
        double currentBalance = getBalance(currency);
        setBalance(currency, currentBalance - balance);
    }

    @Override
    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    @ApiStatus.Internal
    public void setFirstLoginTime(long firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
        this.isDirty = true;
    }

    @Override
    public long getLastLoginTime() {
        return lastLoginTime;
    }

    @Override
    @ApiStatus.Internal
    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        this.isDirty = true;
    }

    @Override
    public boolean isVanished() {
        return isVanished;
    }

    @Override
    public void setVanished(boolean vanished) {
        this.isVanished = vanished;
        this.isDirty = true;
    }

    @Override
    public boolean isFlying() {
        return isFlying;
    }

    @Override
    public void setFlying(boolean flying) {
        this.isFlying = flying;
        this.isDirty = true;
    }

    @Override
    public long getPlayTime() {
        return playTime;
    }

    @ApiStatus.Internal
    public void setPlayTime(long playTime) {
        this.playTime = playTime;
        this.isDirty = true;
    }

    @Override
    public void addPlayTime(long additionalTime) {
        setPlayTime(this.playTime + additionalTime);
    }

    @Override
    public List<Home> getHomes() {
        return new ArrayList<>(homes.values());
    }

    @Override
    public void setHomes(List<Home> homes) {
        this.homes.clear();
        for (Home home : homes) {
            this.homes.put(home.name(), home);
        }
    }

    @Override
    public Home getHome(String homeName) {
        return homes.get(homeName);
    }

    @Override
    public void addHome(Home home) {
        this.homes.put(home.name(), home);
    }

    @Override
    public void removeHome(String homeName) {
        this.homes.remove(homeName);
    }

    @Override
    public long getLastTimeUsedKit(String kitName) {
        return kitCooldowns.getOrDefault(kitName, -1L);
    }

    @Override
    public void setLastTimeUsedKit(String kitName, long timestamp) {
        kitCooldowns.put(kitName, timestamp);
    }

    @Override
    public Map<String, Long> getKitCooldowns() {
        return kitCooldowns;
    }

    @Override
    public Map<String, Object> getCustomData() {
        return customData;
    }

    @Override
    public <T> void setCustomData(String key, T value) {
        customData.put(key, value);
    }

    @Override
    public <T> T getCustomData(String key) {
        return (T) customData.get(key);
    }

    @Override
    public void removeCustomData(String key) {
        customData.remove(key);
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    @Override
    public String toJson() {
        return FancyCorePlugin.GSON.toJson(JsonFancyPlayer.from(this));
    }

}
