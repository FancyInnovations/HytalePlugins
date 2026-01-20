package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerPlayTimeMsPlaceholder implements PlaceholderProvider {

    public static final PlayerPlayTimeMsPlaceholder INSTANCE = new PlayerPlayTimeMsPlaceholder();

    private PlayerPlayTimeMsPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player play time (ms)";
    }

    @Override
    public String getIdentifier() {
        return "player_playtime_ms";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return String.valueOf(player.getData().getPlayTime());
    }
}
