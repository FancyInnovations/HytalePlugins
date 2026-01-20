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

    private FancyPlayerDataImpl(Builder builder) {
        this.uuid = builder.uuid;
        this.username = builder.username;
        this.permissions = builder.permissions != null ? builder.permissions : new ArrayList<>();
        this.groups = builder.groups != null ? builder.groups : new ArrayList<>();
        this.nickname = builder.nickname != null ? builder.nickname : builder.username;
        this.chatColor = builder.chatColor != null ? builder.chatColor : "";
        this.ignoredPlayers = builder.ignoredPlayers != null ? builder.ignoredPlayers : new ArrayList<>();
        this.enabledPrivateMessages = builder.enabledPrivateMessages;
        this.balances = builder.balances != null ? builder.balances : new ConcurrentHashMap<>();
        this.firstLoginTime = builder.firstLoginTime;
        this.lastLoginTime = builder.lastLoginTime;
        this.isVanished = builder.isVanished;
        this.isFlying = builder.isFlying;
        this.playTime = builder.playTime;
        this.customData = builder.customData != null ? builder.customData : new ConcurrentHashMap<>();
        this.homes = new ConcurrentHashMap<>();
        if (builder.homes != null) {
            for (Home home : builder.homes) {
                this.homes.put(home.name(), home);
            }
        }
        this.kitCooldowns = builder.kitCooldowns != null ? new ConcurrentHashMap<>(builder.kitCooldowns) : new ConcurrentHashMap<>();
        this.isDirty = builder.isDirty;
    }

    /**
     * Creates a new builder for FancyPlayerDataImpl.
     *
     * @param uuid     the player's UUID (required)
     * @param username the player's username (required)
     * @return a new Builder instance
     */
    public static Builder builder(UUID uuid, String username) {
        return new Builder(uuid, username);
    }

    /**
     * Builder for FancyPlayerDataImpl.
     * Required fields (uuid, username) are set in the constructor.
     * All other fields have sensible defaults.
     */
    public static class Builder {
        // Required fields
        private final UUID uuid;
        private final String username;

        // Optional fields with defaults
        private List<Permission> permissions = null;
        private List<String> groups = null;
        private String nickname = null;
        private String chatColor = null;
        private List<UUID> ignoredPlayers = null;
        private boolean enabledPrivateMessages = true;
        private Map<Currency, Double> balances = null;
        private long firstLoginTime = System.currentTimeMillis();
        private long lastLoginTime = System.currentTimeMillis();
        private boolean isVanished = false;
        private boolean isFlying = false;
        private long playTime = 0L;
        private List<Home> homes = null;
        private Map<String, Long> kitCooldowns = null;
        private Map<String, Object> customData = null;
        private boolean isDirty = false;

        private Builder(UUID uuid, String username) {
            this.uuid = uuid;
            this.username = username;
        }

        public Builder permissions(List<Permission> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder groups(List<String> groups) {
            this.groups = groups;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder chatColor(String chatColor) {
            this.chatColor = chatColor;
            return this;
        }

        public Builder ignoredPlayers(List<UUID> ignoredPlayers) {
            this.ignoredPlayers = ignoredPlayers;
            return this;
        }

        public Builder enabledPrivateMessages(boolean enabledPrivateMessages) {
            this.enabledPrivateMessages = enabledPrivateMessages;
            return this;
        }

        public Builder balances(Map<Currency, Double> balances) {
            this.balances = balances;
            return this;
        }

        public Builder firstLoginTime(long firstLoginTime) {
            this.firstLoginTime = firstLoginTime;
            return this;
        }

        public Builder lastLoginTime(long lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
            return this;
        }

        public Builder isVanished(boolean isVanished) {
            this.isVanished = isVanished;
            return this;
        }

        public Builder isFlying(boolean isFlying) {
            this.isFlying = isFlying;
            return this;
        }

        public Builder playTime(long playTime) {
            this.playTime = playTime;
            return this;
        }

        public Builder homes(List<Home> homes) {
            this.homes = homes;
            return this;
        }

        public Builder kitCooldowns(Map<String, Long> kitCooldowns) {
            this.kitCooldowns = kitCooldowns;
            return this;
        }

        public Builder customData(Map<String, Object> customData) {
            this.customData = customData;
            return this;
        }

        public Builder isDirty(boolean isDirty) {
            this.isDirty = isDirty;
            return this;
        }

        public FancyPlayerDataImpl build() {
            return new FancyPlayerDataImpl(this);
        }
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
