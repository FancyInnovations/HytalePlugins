package com.fancyinnovations.fancycore.placeholders.builtin.player;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerGroupSuffixPlaceholder implements PlaceholderProvider {

    public static final PlayerGroupSuffixPlaceholder INSTANCE = new PlayerGroupSuffixPlaceholder();

    private PlayerGroupSuffixPlaceholder() {
    }

    @Override
    public String getName() {
        return "Player group suffix";
    }

    @Override
    public String getIdentifier() {
        return "player_group_suffix";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null) {
            return "N/A";
        }

        Group highestGroup = null;
        for (String group : player.getData().getGroups()) {
            Group g = PermissionService.get().getGroup(group);
            if (highestGroup == null || (g != null && g.getWeight() > highestGroup.getWeight())) {
                highestGroup = g;
            }
        }

        if (highestGroup == null) {
            return "N/A";
        }

        return highestGroup.getSuffix();
    }
}
