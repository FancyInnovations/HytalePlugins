package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerBalanceRawPlaceholder implements PlaceholderProvider {

    public static final PlayerBalanceRawPlaceholder INSTANCE = new PlayerBalanceRawPlaceholder();

    private PlayerBalanceRawPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player balance (raw)";
    }

    @Override
    public String getIdentifier() {
        return "player_balance_raw";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return String.valueOf(player.getData().getBalance(FancyCore.get().getCurrencyService().getPrimaryCurrency()));
    }
}
