package com.fancyinnovations.versionchecker;

import com.fancyinnovations.fancyspaces.FancySpaces;
import com.fancyinnovations.fancyspaces.versions.Version;

public class FancySpacesVersionFetcher implements VersionFetcher {

    private final FancySpaces fs;
    private final String spaceID;

    public FancySpacesVersionFetcher(String spaceID) {
        this.spaceID = spaceID;
        this.fs = new FancySpaces();
    }


    @Override
    public FetchedVersion latestVersion() {
        Version version = fs.getVersionService().getLatestVersion(spaceID, "minecraft_plugin", "release");

        return new FetchedVersion(
                version.name(),
                version.publishedAtMillis(),
                version.files().getFirst().url()
        );
    }

    @Override
    public FetchedVersion version(String name) {
        Version version = fs.getVersionService().getVersion(spaceID, name);

        return new FetchedVersion(
                version.name(),
                version.publishedAtMillis(),
                version.files().getFirst().url()
        );
    }
}
