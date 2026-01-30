package com.fancyinnovations.versionchecker.commands;

import com.fancyinnovations.fancyspaces.utils.HttpRequest;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.fancyinnovations.versionchecker.FetchedVersion;
import com.fancyinnovations.versionchecker.VersionChecker;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class UpdateCMD extends CommandBase {

    private final String pluginName;
    private final ExtendedFancyLogger logger;
    private final VersionChecker versionChecker;

    public UpdateCMD(String pluginName, VersionChecker versionChecker, ExtendedFancyLogger logger, String permission)  {
        super("update", "Downloads latest version of " + pluginName);
        this.pluginName = pluginName;
        this.logger = logger;
        this.versionChecker = versionChecker;

        if (permission != null && !permission.isEmpty()) {
            requirePermission(permission);
        }
    }

    public UpdateCMD(String pluginName, VersionChecker versionChecker, ExtendedFancyLogger logger) {
        this(pluginName, versionChecker, logger, null);
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FetchedVersion latestVersion = this.versionChecker.check();
        if (latestVersion == null) {
            ctx.sender().sendMessage(
                    Message.raw("You are already using the latest version of " + pluginName + ".")
            );
            logger.info(pluginName + " is already up to date.");
            return;
        }

        ctx.sender().sendMessage(
                Message.raw("A new version of " + pluginName + " is available: " + latestVersion.name() + ". It will be downloaded now. Please restart the server after the download is complete.")
        );
        logger.info(
                "A new version of " + pluginName + " is available",
                StringProperty.of("version", latestVersion.name()),
                StringProperty.of("published_at", TimeUtils.formatDate(latestVersion.publishedAt())),
                StringProperty.of("download_link", latestVersion.downloadURL())
        );

        ctx.sender().sendMessage(
                Message.raw("Downloading " + pluginName + " version " + latestVersion.name() + "...")
        );
        logger.info(
                "Starting to download the latest version of " + pluginName + "...",
                StringProperty.of("version", latestVersion.name())
        );

        HttpRequest req = new HttpRequest(latestVersion.downloadURL())
                .withMethod("GET")
                .withHeader("User-Agent", pluginName + "-PluginUpdater")
                .withBodyHandler(HttpResponse.BodyHandlers.ofByteArray());

        try {
            File modsDir = new File("mods");
            for (File file : modsDir.listFiles()) {
                if (file.getName().toLowerCase().startsWith(pluginName.toLowerCase()) && file.getName().endsWith(".jar")) {
                    Files.delete(file.toPath());
                }
            }

            HttpResponse<byte[]> resp = req.send();
            Files.write(Path.of("mods/" + pluginName + "-" + latestVersion.name() + ".jar"), resp.body());

            ctx.sender().sendMessage(
                    Message.raw("Successfully downloaded " + pluginName + " version " + latestVersion.name() + ". Please restart the server to apply the update.")
            );
            logger.info(
                    "Successfully downloaded the latest version of " + pluginName,
                    StringProperty.of("version", latestVersion.name()),
                    StringProperty.of("published_at", TimeUtils.formatDate(latestVersion.publishedAt())),
                    StringProperty.of("download_link", latestVersion.downloadURL())
            );

        } catch (URISyntaxException | IOException e) {
            ctx.sender().sendMessage(
                    Message.raw("Failed to download the latest version of " + pluginName + ". Please check the logs for more information.")
            );
            logger.error(
                    "Failed to download the latest version of " + pluginName,
                    StringProperty.of("version", latestVersion.name()),
                    StringProperty.of("published_at", TimeUtils.formatDate(latestVersion.publishedAt())),
                    StringProperty.of("download_link", latestVersion.downloadURL()),
                    ThrowableProperty.of(e)
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ctx.sender().sendMessage(
                    Message.raw("Failed to download the latest version of " + pluginName + ". Please check the logs for more information.")
            );
            logger.error(
                    "Failed to download the latest version of " + pluginName,
                    StringProperty.of("version", latestVersion.name()),
                    StringProperty.of("published_at", TimeUtils.formatDate(latestVersion.publishedAt())),
                    StringProperty.of("download_link", latestVersion.downloadURL()),
                    ThrowableProperty.of(e)
            );
        }
    }
}
