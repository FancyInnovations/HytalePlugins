package com.fancyinnovations.fancycore.api.scoreboard;

import com.fancyinnovations.fancycore.api.FancyCore;

import java.util.List;

public interface ScoreboardService {

    static ScoreboardService get() {
        return FancyCore.get().getScoreboardService();
    }

    ScoreboardPage getPage(String name);

    List<ScoreboardPage> getPages();

    void createPage(ScoreboardPage page);

    void deletePage(String name);

}
