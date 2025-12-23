package com.fancyinnovations.fancycore.api.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface FancyPlayerService {

    static FancyPlayerService get() {
        return FancyCore.get().getPlayerService();
    }

    @Nullable FancyPlayer getByUUID(UUID uuid);

    @Nullable FancyPlayer getByUsername(String username);

    List<FancyPlayer> getOnlinePlayers();

    @ApiStatus.Internal
    FancyPlayerData fromJson(String json);
}
