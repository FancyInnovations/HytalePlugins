package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerNickNamePlaceholder implements PlaceholderProvider {

    public static final PlayerNickNamePlaceholder INSTANCE = new PlayerNickNamePlaceholder();

    private PlayerNickNamePlaceholder() {
    }

    @Override
    public String getName() {
        return "Player nickname";
    }

    @Override
    public String getIdentifier() {
        return "player_nickname";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return player.getData().getNickname();
    }
}
