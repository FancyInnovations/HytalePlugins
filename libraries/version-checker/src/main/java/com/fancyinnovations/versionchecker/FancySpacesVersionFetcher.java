package com.fancyinnovations.versionchecker;

import com.fancyinnovations.fancyspaces.FancySpaces;
import com.fancyinnovations.fancyspaces.versions.Version;

public class FancySpacesVersionFetcher implements VersionFetcher {

    private final FancySpaces fs;
    private final String spaceID;
    private final String channel;

    public FancySpacesVersionFetcher(String spaceID, String channel) {
        this.spaceID = spaceID;
        this.channel = channel;
        this.fs = new FancySpaces();
    }

    public FancySpacesVersionFetcher(String spaceID) {
        this(spaceID, "");
    }

    @Override
    public FetchedVersion latestVersion() {
        Version version = fs.getVersionService().getLatestVersion(spaceID, "hytale_plugin", channel);

        return new FetchedVersion(
                version.name(),
                version.publishedAtMillis(),
                version.files().getFirst().url()
        );
    }

    @Override
    public FetchedVersion version(String name) {
        Version version = fs.getVersionService().getVersion(spaceID, name);
        if (version == null) {
            return new FetchedVersion(
                    "unknown",
                    0,
                    "unknown"
            );
        }

        return new FetchedVersion(
                version.name(),
                version.publishedAtMillis(),
                version.files().getFirst().url()
        );
    }
}
