package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerIsOPPlaceholder implements PlaceholderProvider {

    public static final PlayerIsOPPlaceholder INSTANCE = new PlayerIsOPPlaceholder();

    private PlayerIsOPPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player is OP";
    }

    @Override
    public String getIdentifier() {
        return "player_is_op";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        boolean hasOP = PermissionsModule.get().hasPermission(player.getData().getUUID(), "*");
        return Boolean.toString(hasOP);
    }
}
