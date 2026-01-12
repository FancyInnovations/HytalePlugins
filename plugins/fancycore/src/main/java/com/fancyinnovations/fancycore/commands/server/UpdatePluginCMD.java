package com.fancyinnovations.fancycore.commands.server;

import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import com.fancyinnovations.fancyspaces.utils.HttpRequest;
import com.fancyinnovations.versionchecker.FetchedVersion;
import com.fancyinnovations.versionchecker.VersionChecker;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class UpdatePluginCMD {

    private final ExtendedFancyLogger logger;
    private final VersionChecker versionChecker;

    public UpdatePluginCMD() {
        this.logger = FancyCorePlugin.get().getFancyLogger();
        this.versionChecker = FancyCorePlugin.get().getVersionChecker();
    }

    protected void execute() {
        FetchedVersion latestVersion = this.versionChecker.check();
        if (latestVersion == null) {
            // TODO (HTEA): send message that plugin is up to date
            logger.info("FancyCore is up to date");
            return;
        }

        // TODO (HTEA): send message that a new version is available and inform that it will be downloaded
        logger.info(
                "A new version of FancyCore is available",
                StringProperty.of("version", latestVersion.name()),
                StringProperty.of("published_at", TimeUtils.formatDate(latestVersion.publishedAt())),
                StringProperty.of("download_link", latestVersion.downloadURL())
        );

        logger.info(
                "Starting to download the latest version of FancyCore...",
                StringProperty.of("version", latestVersion.name())
        );

        HttpRequest req = new HttpRequest(latestVersion.downloadURL())
                .withMethod("GET")
                .withHeader("User-Agent", "FancyCore-PluginUpdater")
                .withBodyHandler(HttpResponse.BodyHandlers.ofByteArray());

        try {
            HttpResponse<byte[]> resp = req.send();
            Files.write(Path.of("mods/FancyCore.jar"), resp.body()); // TODO (HTEA): check if path is correct

            logger.info(
                    "Successfully downloaded the latest version of FancyCore",
                    StringProperty.of("version", latestVersion.name()),
                    StringProperty.of("published_at", TimeUtils.formatDate(latestVersion.publishedAt())),
                    StringProperty.of("download_link", latestVersion.downloadURL())
            );
            // TODO (HTEA): send message that download is complete and that a server restart is required

            // TODO (HTEA): investigate if we can hot-reload the plugin

        } catch (URISyntaxException | IOException | InterruptedException e) {
            logger.error(
                    "Failed to download the latest version of FancyCore",
                    StringProperty.of("version", latestVersion.name()),
                    StringProperty.of("published_at", TimeUtils.formatDate(latestVersion.publishedAt())),
                    StringProperty.of("download_link", latestVersion.downloadURL()),
                    ThrowableProperty.of(e)
            );
        }
    }

}
