package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerUuidPlaceholder implements PlaceholderProvider {

    public static final PlayerUuidPlaceholder INSTANCE = new PlayerUuidPlaceholder();

    private PlayerUuidPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player UUID";
    }

    @Override
    public String getIdentifier() {
        return "player_uuid";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return player.getData().getUUID().toString();
    }
}
