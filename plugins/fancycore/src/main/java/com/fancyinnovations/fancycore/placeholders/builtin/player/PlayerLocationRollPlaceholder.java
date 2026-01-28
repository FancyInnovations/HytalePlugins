package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.utils.ComponentProvider;
import com.fancyinnovations.hytaleutils.NumberUtils;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerLocationRollPlaceholder implements PlaceholderProvider {

    public static final PlayerLocationRollPlaceholder INSTANCE = new PlayerLocationRollPlaceholder();

    private PlayerLocationRollPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player location roll";
    }

    @Override
    public String getIdentifier() {
        return "player_location_roll";
    }

    @Override
    public String parse(@Nullable FancyPlayer fp, @NotNull String input) {
        if (fp == null) {
            return "N/A";
        }

        TransformComponent transform = ComponentProvider.toTransform(fp);
        if (transform == null) {
            return "N/A";
        }

        return NumberUtils.FORMAT_TWO_DECIMALS.format(transform.getRotation().getRoll());
    }
}
