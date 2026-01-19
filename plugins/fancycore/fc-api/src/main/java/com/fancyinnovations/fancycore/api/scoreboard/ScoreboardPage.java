package com.fancyinnovations.fancycore.api.scoreboard;

import java.util.List;

public interface ScoreboardPage {

    String getName();

    String getAlignment();

    void setAlignment(String alignment);

    int getOffset();

    void setOffset(int offset);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    BackgroundColor getBackgroundColor();

    void setBackgroundColor(BackgroundColor backgroundColor);

    List<ScoreboardLine> getLines();

    void setLines(List<ScoreboardLine> lines);

    void addLine(ScoreboardLine line);

    void removeLine(ScoreboardLine line);

    public record BackgroundColor(
            byte alpha,
            byte red,
            byte green,
            byte blue
    ) {
    }

}
