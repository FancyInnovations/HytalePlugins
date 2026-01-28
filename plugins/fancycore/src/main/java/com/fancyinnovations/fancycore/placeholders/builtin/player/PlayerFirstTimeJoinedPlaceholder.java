package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.hytaleutils.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFirstTimeJoinedPlaceholder implements PlaceholderProvider {

    public static final PlayerFirstTimeJoinedPlaceholder INSTANCE = new PlayerFirstTimeJoinedPlaceholder();

    private PlayerFirstTimeJoinedPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player first time joined";
    }

    @Override
    public String getIdentifier() {
        return "player_first_time_joined";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return TimeUtils.formatDate(player.getData().getFirstLoginTime());
    }
}
