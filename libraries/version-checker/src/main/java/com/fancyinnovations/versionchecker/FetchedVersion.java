package com.fancyinnovations.versionchecker;

public record FetchedVersion(
        String name,
        long publishedAt,
        String downloadURL
) {

    public boolean isNewerThan(FetchedVersion other) {
        return other.publishedAt > this.publishedAt;
    }
}
