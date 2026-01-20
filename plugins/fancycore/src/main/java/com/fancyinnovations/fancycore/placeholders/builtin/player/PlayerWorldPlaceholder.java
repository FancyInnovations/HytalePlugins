package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerWorldPlaceholder implements PlaceholderProvider {

    public static final PlayerWorldPlaceholder INSTANCE = new PlayerWorldPlaceholder();

    private PlayerWorldPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player world";
    }

    @Override
    public String getIdentifier() {
        return "player_world";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        if (player.getPlayer().getWorldUuid() == null) {
            return "N/A";
        }

        World world = Universe.get().getWorld(player.getPlayer().getWorldUuid());
        if (world == null) {
            return "N/A";
        }

        return world.getName();
    }
}
