package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerPlayTimeFormattedPlaceholder implements PlaceholderProvider {

    public static final PlayerPlayTimeFormattedPlaceholder INSTANCE = new PlayerPlayTimeFormattedPlaceholder();

    private PlayerPlayTimeFormattedPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player play time (formatted)";
    }

    @Override
    public String getIdentifier() {
        return "player_playtime";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        long playTime = player.getData().getPlayTime();
        if (player.isOnline()) {
            long currentSessionTime = System.currentTimeMillis() - player.getJoinedAt();
            playTime += currentSessionTime;
        }

        return TimeUtils.formatTime(playTime);
    }
}
