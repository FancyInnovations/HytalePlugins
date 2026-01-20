package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.utils.ComponentProvider;
import com.fancyinnovations.fancycore.utils.NumberUtils;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerLocationPitchPlaceholder implements PlaceholderProvider {

    public static final PlayerLocationPitchPlaceholder INSTANCE = new PlayerLocationPitchPlaceholder();

    private PlayerLocationPitchPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player location pitch";
    }

    @Override
    public String getIdentifier() {
        return "player_location_pitch";
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

        return NumberUtils.FORMAT_TWO_DECIMALS.format(transform.getRotation().getPitch());
    }
}
