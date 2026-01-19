package com.fancyinnovations.fancycore.api.scoreboard;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public interface ScoreboardStorage {

    @ApiStatus.Internal
    ScoreboardPage getPage(String name);

    @ApiStatus.Internal
    List<ScoreboardPage> loadAllPages();

    @ApiStatus.Internal
    void storePage(ScoreboardPage page);

    @ApiStatus.Internal
    void deletePage(String name);

    @ApiStatus.Internal
    int countPages();
}
