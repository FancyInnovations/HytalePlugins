package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerNamePlaceholder implements PlaceholderProvider {

    public static final PlayerNamePlaceholder INSTANCE = new PlayerNamePlaceholder();

    private PlayerNamePlaceholder() {
    }

    @Override
    public String getName() {
        return "Player name";
    }

    @Override
    public String getIdentifier() {
        return "player_name";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return player.getData().getUsername();
    }
}
