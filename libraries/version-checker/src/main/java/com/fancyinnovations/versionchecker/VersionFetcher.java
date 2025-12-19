package com.fancyinnovations.versionchecker;

public interface VersionFetcher {

    FetchedVersion latestVersion();

    FetchedVersion version(String name);

}
