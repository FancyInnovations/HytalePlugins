package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFirstTimeJoinedRawPlaceholder implements PlaceholderProvider {

    public static final PlayerFirstTimeJoinedRawPlaceholder INSTANCE = new PlayerFirstTimeJoinedRawPlaceholder();

    private PlayerFirstTimeJoinedRawPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player first time joined (raw timestamp)";
    }

    @Override
    public String getIdentifier() {
        return "player_first_time_joined_raw";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return String.valueOf(player.getData().getFirstLoginTime());
    }
}
