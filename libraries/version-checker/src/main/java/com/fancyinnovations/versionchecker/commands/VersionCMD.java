package com.fancyinnovations.versionchecker.commands;

import com.fancyinnovations.hytaleutils.TimeUtils;
import com.fancyinnovations.versionchecker.FetchedVersion;
import com.fancyinnovations.versionchecker.VersionConfig;
import com.fancyinnovations.versionchecker.VersionFetcher;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class VersionCMD extends CommandBase {

    private final String pluginName;
    private final VersionFetcher versionFetcher;

    public VersionCMD(String pluginName, VersionFetcher versionFetcher, String permission) {
        super("version", "Displays the current version of " + pluginName + " and latest version available");
        this.pluginName = pluginName;
        this.versionFetcher = versionFetcher;

        if (permission != null && !permission.isEmpty()) {
            requirePermission(permission);
        }
    }

    public VersionCMD(String pluginName, VersionFetcher versionFetcher) {
        this(pluginName, versionFetcher, null);
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FetchedVersion currentVersion = versionFetcher.version(VersionConfig.loadVersionConfig().version());
        FetchedVersion latestVersion = versionFetcher.latestVersion();

        ctx.sendMessage(Message.raw("Current " + pluginName + " version: " + currentVersion.name() + " (published " + TimeUtils.formatDate(currentVersion.publishedAt()) + ")"));
        ctx.sendMessage(Message.raw("Latest " + pluginName + " version: " + latestVersion.name() + " (published " + TimeUtils.formatDate(latestVersion.publishedAt()) + ")"));
        if (latestVersion.isNewerThan(currentVersion)) {
            ctx.sendMessage(Message.raw("A new version of " + pluginName + " is available! Please consider updating."));
        }
    }
}
