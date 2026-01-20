package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerChatColorPlaceholder implements PlaceholderProvider {

    public static final PlayerChatColorPlaceholder INSTANCE = new PlayerChatColorPlaceholder();

    private PlayerChatColorPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player chat color";
    }

    @Override
    public String getIdentifier() {
        return "player_chat_color";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return player.getData().getChatColor();
    }
}
