package com.fancyinnovations.versionchecker;

import com.fancyinnovations.versionchecker.utils.ResourceUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public record VersionConfig(
        String version,
        String channel,
        String commit
) {

    private static final Gson GSON = new Gson();

    public static VersionConfig loadVersionConfig() {
        String fileStr = ResourceUtils.readResourceAsString("version.json");
        System.out.println("Loaded version config: " + fileStr);
        return GSON.fromJson(fileStr, VersionConfig.class);
    }

    public static VersionConfig loadVersionConfig(String path) throws IOException {
        File file = new File(path);
        String fileStr = Files.readString(file.toPath());
        return GSON.fromJson(fileStr, VersionConfig.class);
    }

    public void saveFile(String path) throws IOException {
        File file = new File(path);
        file.mkdirs();

        String json = GSON.toJson(this);
        Files.write(file.toPath(), json.getBytes());
    }
}
