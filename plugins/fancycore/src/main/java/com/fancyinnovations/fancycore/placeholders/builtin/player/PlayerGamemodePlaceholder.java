package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.utils.ComponentProvider;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerGamemodePlaceholder implements PlaceholderProvider {

    public static final PlayerGamemodePlaceholder INSTANCE = new PlayerGamemodePlaceholder();

    private PlayerGamemodePlaceholder() {
    }

    @Override
    public String getName() {
        return "Player gamemode";
    }

    @Override
    public String getIdentifier() {
        return "player_gamemode";
    }

    @Override
    public String parse(@Nullable FancyPlayer fp, @NotNull String input) {
        if (fp == null) {
            return "N/A";
        }

        Player player = ComponentProvider.toPlayer(fp);
        if (player == null) {
            return "N/A";
        }

        GameMode gamemode = player.getGameMode();
        return gamemode.name();
    }
}
