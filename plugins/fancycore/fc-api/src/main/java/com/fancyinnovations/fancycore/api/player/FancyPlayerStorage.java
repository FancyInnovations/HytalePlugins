package com.fancyinnovations.fancycore.api.player;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.UUID;

@ApiStatus.Internal
public interface FancyPlayerStorage {

    @ApiStatus.Internal
    void savePlayer(FancyPlayerData player);

    @ApiStatus.Internal
    FancyPlayerData loadPlayer(UUID uuid);

    @ApiStatus.Internal
    FancyPlayerData loadPlayerByUsername(String username);

    @ApiStatus.Internal
    List<FancyPlayerData> loadAllPlayers();

    @ApiStatus.Internal
    void deletePlayer(UUID uuid);

    @ApiStatus.Internal
    int countPlayers();
}
