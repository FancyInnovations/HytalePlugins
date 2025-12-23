package com.fancyinnovations.fancycore.player.service;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.FancyPlayerImpl;
import com.fancyinnovations.fancycore.player.storage.json.JsonFancyPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FancyPlayerServiceImpl implements FancyPlayerService {

    private final Map<UUID, FancyPlayer> cache;
    private final Set<FancyPlayer> onlinePlayers;
    private final FancyPlayerStorage storage;

    public FancyPlayerServiceImpl() {
        this.cache = new ConcurrentHashMap<>();
        this.onlinePlayers = new HashSet<>();
        this.storage = FancyCorePlugin.get().getPlayerStorage();
    }

    @Override
    public FancyPlayer getByUUID(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        return tryToGetFromStorage(uuid);
    }

    @Override
    public FancyPlayer getByUsername(String username) {
        for (FancyPlayer fp : cache.values()) {
            if (fp.getData().getUsername().equalsIgnoreCase(username)) {
                return fp;
            }
        }

        return tryToGetFromStorage(username);
    }

    @Override
    public List<FancyPlayer> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers);
    }

    @Override
    public FancyPlayerData fromJson(String json) {
        return FancyCorePlugin.GSON.fromJson(json, JsonFancyPlayer.class).toFancyPlayer();
    }

    @ApiStatus.Internal
    public void addOnlinePlayer(FancyPlayer player) {
        onlinePlayers.add(player);
    }

    @ApiStatus.Internal
    public void removeOnlinePlayer(FancyPlayer player) {
        onlinePlayers.remove(player);
    }

    @ApiStatus.Internal
    public List<FancyPlayer> getAllCached() {
        return new ArrayList<>(cache.values());
    }

    public void addPlayerToCache(FancyPlayer player) {
        cache.put(player.getData().getUUID(), player);
    }

    public FancyPlayer tryToGetFromStorage(UUID uuid) {
        try {
            FancyPlayerData data = storage.loadPlayer(uuid);
            if (data == null) {
                return null;
            }

            FancyPlayer fancyPlayer = new FancyPlayerImpl(data);
            addPlayerToCache(fancyPlayer);
            return fancyPlayer;
        } catch (Exception e) {
            return null;
        }
    }

    public FancyPlayer tryToGetFromStorage(String username) {
        try {
            FancyPlayerData data = storage.loadPlayerByUsername(username);
            if (data == null) {
                return null;
            }

            FancyPlayer fancyPlayer = new FancyPlayerImpl(data);
            addPlayerToCache(fancyPlayer);
            return fancyPlayer;
        } catch (Exception e) {
            return null;
        }
    }

    public void removePlayerFromCache(UUID uuid) {
        cache.remove(uuid);
    }

}
