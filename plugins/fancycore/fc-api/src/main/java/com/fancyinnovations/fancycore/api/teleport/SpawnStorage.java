package com.fancyinnovations.fancycore.api.teleport;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SpawnStorage {

    @ApiStatus.Internal
    SpawnLocation loadSpawnLocation();

    @ApiStatus.Internal
    void storeSpawnLocation(SpawnLocation location);

    @ApiStatus.Internal
    void deleteSpawnLocation();

}
