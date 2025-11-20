package com.fancyinnovations.fancycore.player.storage.fake;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerStorage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FancyPlayerFakeStorage implements FancyPlayerStorage {

    private final Map<UUID, FancyPlayer> players;

    public FancyPlayerFakeStorage() {
        players = new ConcurrentHashMap<>();
    }

    @Override
    public void savePlayer(FancyPlayer player) {
        players.put(player.getUUID(), player);
    }

    @Override
    public FancyPlayer loadPlayer(UUID uuid) {
        return players.get(uuid);
    }

    @Override
    public void deletePlayer(UUID uuid) {
        players.remove(uuid);
    }

    @Override
    public int countPlayers() {
        return players.size();
    }
}
