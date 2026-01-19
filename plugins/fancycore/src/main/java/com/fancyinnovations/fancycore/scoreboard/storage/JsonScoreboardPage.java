package com.fancyinnovations.fancycore.scoreboard.storage;

import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardLine;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.scoreboard.ScoreboardPageImpl;

import java.util.ArrayList;
import java.util.List;

public record JsonScoreboardPage(
        String name,
        String alignment,
        int offset,
        int width,
        int height,
        ScoreboardPage.BackgroundColor backgroundColor,
        List<JsonScoreboardLine> lines
) {

    public static JsonScoreboardPage fromScoreboardPage(ScoreboardPage page) {
        List<JsonScoreboardLine> jsonLines = new ArrayList<>();
        for (var line : page.getLines()) {
            jsonLines.add(JsonScoreboardLine.fromScoreboardLine(line));
        }

        return new JsonScoreboardPage(
                page.getName(),
                page.getAlignment(),
                page.getOffset(),
                page.getWidth(),
                page.getHeight(),
                page.getBackgroundColor(),
                jsonLines
        );
    }

    public ScoreboardPage toScoreboardPage() {
        List<ScoreboardLine> scoreboardLines = new ArrayList<>();
        if (lines != null) {
            for (var jsonLine : lines) {
                scoreboardLines.add(jsonLine.toScoreboardLine());
            }
        }

        return new ScoreboardPageImpl(
                name,
                alignment,
                offset,
                width,
                height,
                backgroundColor,
                scoreboardLines
        );
    }

}
