package com.fancyinnovations.versionchecker;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancyanalytics.sdk.ApiClient;
import de.oliver.fancyanalytics.sdk.events.Event;

import java.io.IOException;
import java.util.HashMap;

public class VersionChecker {

    private final ExtendedFancyLogger fancyLogger;
    private final String pluginName;
    private final VersionFetcher versionFetcher;
    private final VersionConfig versionConfig;

    public VersionChecker(ExtendedFancyLogger fancyLogger, String pluginName, VersionFetcher versionFetcher) {
        this.fancyLogger = fancyLogger;
        this.pluginName = pluginName;
        this.versionFetcher = versionFetcher;

        this.versionConfig = VersionConfig.loadVersionConfig();
    }

    /**
     * Checks if a new (release) version is available.
     *
     * @return The latest version if a new version is available, null otherwise.
     */
    public FetchedVersion check() {
        FetchedVersion latestVersion = versionFetcher.latestVersion();
        FetchedVersion currentVersion = versionFetcher.version(versionConfig.version());
        if (latestVersion.isNewerThan(currentVersion)) {
            return latestVersion;
        }

        return null;
    }

    /**
     * Checks if the latest (release) version is installed.
     * Sends a warning message to console if not.
     */
    public void checkForConsole() {
        FetchedVersion latestVersion = versionFetcher.latestVersion();
        FetchedVersion currentVersion = versionFetcher.version(versionConfig.version());
        if (latestVersion.isNewerThan(currentVersion)) {
            fancyLogger.warn(
                    "You are using an outdated version of "+pluginName+". Please consider updating to the latest version.",
                    StringProperty.of("plugin", pluginName),
                    StringProperty.of("current_version", currentVersion.name()),
                    StringProperty.of("latest_version", latestVersion.name()),
                    StringProperty.of("download_url", latestVersion.downloadURL())
            );
        }
    }

    /**
     * Checks if a new version of the plugin got installed.
     * If a new version was installed, a event to FancyAnalytics is being sent.
     */
    public void checkPluginVersionChanged(ApiClient apiClient, String faProjectID) {
        try {
            VersionConfig oldVersionConfig = VersionConfig.loadVersionConfig("mods/" + pluginName + "/version.json");

            if (versionConfig.version().equals(oldVersionConfig.version())) {
                return;
            }

            versionConfig.saveFile("mods/" + pluginName + "/version.json");

            Event event = new Event("PluginVersionChanged", new HashMap<>())
                    .withProperty("from", oldVersionConfig.version())
                    .withProperty("to", versionConfig.version());

            apiClient.getEventService().createEvent(faProjectID, event);


        } catch (IOException e) {
            fancyLogger.warn(
                    "Failed to load version config",
                    ThrowableProperty.of(e)
            );
        }
    }

}
