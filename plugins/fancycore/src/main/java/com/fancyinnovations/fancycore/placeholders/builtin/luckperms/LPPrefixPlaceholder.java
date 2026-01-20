package com.fancyinnovations.fancycore.placeholders.builtin.luckperms;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderProvider;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LPPrefixPlaceholder implements PlaceholderProvider {

    public static final LPPrefixPlaceholder INSTANCE = new LPPrefixPlaceholder();

    private LuckPerms luckPerms = null;

    private LPPrefixPlaceholder() {
        if (PluginManager.get().getPlugin(PluginIdentifier.fromString("LuckPerms:LuckPerms")) != null) {
            luckPerms = LuckPermsProvider.get();
        }
    }

    @Override
    public String getName() {
        return "LuckPerms player prefix";
    }

    @Override
    public String getIdentifier() {
        return "luckperms_prefix";
    }

    @Override
    public String parse(@Nullable FancyPlayer player, @NotNull String input) {
        if (player == null || luckPerms == null) {
            return "N/A";
        }

        User user = luckPerms.getUserManager().getUser(player.getData().getUUID());
        return user.getCachedData().getMetaData().getPrefix();
    }

}
