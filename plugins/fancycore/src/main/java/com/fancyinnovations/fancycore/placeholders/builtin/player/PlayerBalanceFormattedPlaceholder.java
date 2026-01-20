package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.utils.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerBalanceFormattedPlaceholder implements PlaceholderProvider {

    public static final PlayerBalanceFormattedPlaceholder INSTANCE = new PlayerBalanceFormattedPlaceholder();

    private PlayerBalanceFormattedPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player balance (formatted)";
    }

    @Override
    public String getIdentifier() {
        return "player_balance";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        return NumberUtils.formatNumber(player.getData().getBalance(FancyCore.get().getCurrencyService().getPrimaryCurrency()));
    }
}
