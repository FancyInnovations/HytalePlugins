package com.fancyinnovations.fancycore.api.player;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface FancyPlayerService {

    @Nullable FancyPlayer getByUUID(UUID uuid);

    @Nullable FancyPlayer getByUsername(String username);

    List<FancyPlayer> getOnlinePlayers();
}
