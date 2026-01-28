package com.fancyinnovations.fancycore.commands.fancycore;

import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.fancyinnovations.versionchecker.FetchedVersion;
import com.fancyinnovations.versionchecker.VersionConfig;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class FancyCoreVersionCMD extends CommandBase {

    protected FancyCoreVersionCMD() {
        super("version", "Displays the current version of FancyCore");
        requirePermission("fancycore.commands.fancycore.version");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FetchedVersion currentVersion = FancyCorePlugin.get().getVersionFetcher().version(VersionConfig.loadVersionConfig().version());
        FetchedVersion latestVersion = FancyCorePlugin.get().getVersionFetcher().latestVersion();

        ctx.sendMessage(Message.raw("Current FancyCore version: " + currentVersion.name() + " (published " + TimeUtils.formatDate(currentVersion.publishedAt()) + ")"));
        ctx.sendMessage(Message.raw("Latest FancyCore version: " + latestVersion.name() + " (published " + TimeUtils.formatDate(latestVersion.publishedAt()) + ")"));
        if (latestVersion.isNewerThan(currentVersion)) {
            ctx.sendMessage(Message.raw("A new version of FancyCore is available! You can automatically update using /fancycore update"));
        }
    }
}
