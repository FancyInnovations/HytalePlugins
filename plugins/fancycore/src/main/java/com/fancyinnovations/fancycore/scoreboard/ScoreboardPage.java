package com.fancyinnovations.fancycore.scoreboard;

import java.util.List;

public class ScoreboardPage {

    private final String name;
    private String alignment;
    private int offset;
    private int width;
    private int height;
    private List<ScoreboardLine> lines;

    public ScoreboardPage(String name, String alignment, int offset, int width, int height, List<ScoreboardLine> lines) {
        this.name = name;
        this.alignment = alignment;
        this.offset = offset;
        this.width = width;
        this.height = height;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<ScoreboardLine> getLines() {
        return lines;
    }

    public void setLines(List<ScoreboardLine> lines) {
        this.lines = lines;
    }

    public void addLine(ScoreboardLine line) {
        this.lines.add(line);
    }

    public void removeLine(ScoreboardLine line) {
        this.lines.remove(line);
    }

}
