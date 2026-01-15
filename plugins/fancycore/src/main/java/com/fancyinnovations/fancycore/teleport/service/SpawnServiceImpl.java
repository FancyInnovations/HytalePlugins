package com.fancyinnovations.fancycore.teleport.service;

import com.fancyinnovations.fancycore.api.teleport.SpawnLocation;
import com.fancyinnovations.fancycore.api.teleport.SpawnService;
import com.fancyinnovations.fancycore.api.teleport.SpawnStorage;

public class SpawnServiceImpl implements SpawnService {

    private final SpawnStorage storage;
    private SpawnLocation cachedLocation;

    public SpawnServiceImpl(SpawnStorage storage) {
        this.storage = storage;

        this.cachedLocation = storage.loadSpawnLocation();
    }

    @Override
    public SpawnLocation getSpawnLocation() {
        return this.cachedLocation;
    }

    @Override
    public void setSpawnLocation(SpawnLocation location) {
        this.cachedLocation = location;
        this.storage.storeSpawnLocation(location);
    }

    @Override
    public void removeSpawnLocation() {
        this.cachedLocation = null;
        this.storage.deleteSpawnLocation();
    }
}
