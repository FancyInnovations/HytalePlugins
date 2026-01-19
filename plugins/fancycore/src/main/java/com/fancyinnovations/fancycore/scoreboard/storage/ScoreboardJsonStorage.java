package com.fancyinnovations.fancycore.scoreboard.storage;

import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardJsonStorage implements ScoreboardStorage {

    private static final String DATA_DIR_PATH = "mods/FancyCore/data/scoreboards";
    private final JDB db;

    public ScoreboardJsonStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    @Override
    public ScoreboardPage getPage(String name) {
        try {
            JsonScoreboardPage jsonPage = db.get(name, JsonScoreboardPage.class);
            if (jsonPage != null) {
                return jsonPage.toScoreboardPage();
            }
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load ScoreboardPage: " + name,
                    ThrowableProperty.of(e)
            );
        }

        return null;
    }

    @Override
    public List<ScoreboardPage> loadAllPages() {
        try {
            List<JsonScoreboardPage> jsonPages = db.getAll("", JsonScoreboardPage.class);
            List<ScoreboardPage> pages = new ArrayList<>();
            for (var jsonPage : jsonPages) {
                pages.add(jsonPage.toScoreboardPage());
            }

            return pages;
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load all ScoreboardPages",
                    ThrowableProperty.of(e)
            );
        }

        return new ArrayList<>();
    }

    @Override
    public void storePage(ScoreboardPage page) {
        try {
            JsonScoreboardPage jsonPage = JsonScoreboardPage.fromScoreboardPage(page);
            db.set(page.getName(), jsonPage);
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store ScoreboardPage: " + page.getName(),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public void deletePage(String name) {
        db.delete(name);
    }

    @Override
    public int countPages() {
        File dataDir = new File(DATA_DIR_PATH);
        String[] files = dataDir.list((_, filename) -> filename.endsWith(".json"));
        return files != null ? files.length : 0;
    }
}
