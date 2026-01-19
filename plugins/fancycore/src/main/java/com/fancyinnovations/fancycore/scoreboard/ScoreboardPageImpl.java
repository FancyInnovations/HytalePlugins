package com.fancyinnovations.fancycore.scoreboard;

import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardLine;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;

import java.util.List;

public class ScoreboardPageImpl implements ScoreboardPage{

    private final String name;
    private String alignment;
    private int offset;
    private int width;
    private int height;
    private ScoreboardPage.BackgroundColor backgroundColor;
    private List<ScoreboardLine> lines;

    public ScoreboardPageImpl(String name, String alignment, int offset, int width, int height, ScoreboardPage.BackgroundColor backgroundColor, List<ScoreboardLine> lines) {
        this.name = name;
        this.alignment = alignment;
        this.offset = offset;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.lines = lines;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAlignment() {
        return alignment;
    }

    @Override
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public ScoreboardPage.BackgroundColor getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(ScoreboardPage.BackgroundColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public List<ScoreboardLine> getLines() {
        return lines;
    }

    @Override
    public void setLines(List<ScoreboardLine> lines) {
        this.lines = lines;
    }

    @Override
    public void addLine(ScoreboardLine line) {
        this.lines.add(line);
    }

    @Override
    public void removeLine(ScoreboardLine line) {
        this.lines.remove(line);
    }

}
