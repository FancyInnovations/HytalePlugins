package com.fancyinnovations.fancycore.api.teleport;

import com.fancyinnovations.fancycore.api.FancyCore;

public interface SpawnService {

    static SpawnService get() {
        return FancyCore.get().getSpawnService();
    }

    SpawnLocation getSpawnLocation();

    void setSpawnLocation(SpawnLocation location);

    void removeSpawnLocation();

}
