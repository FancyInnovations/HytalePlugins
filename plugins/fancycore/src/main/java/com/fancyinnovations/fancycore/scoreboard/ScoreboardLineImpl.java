package com.fancyinnovations.fancycore.scoreboard;

import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardLine;

public class ScoreboardLineImpl implements ScoreboardLine {

    private String content;
    private String alignment;
    private Integer fontSize;
    private Integer paddingTop;
    private Integer paddingBottom;
    private Integer paddingLeft;
    private Integer paddingRight;

    public ScoreboardLineImpl(
            String content,
            String alignment,
            Integer fontSize,
            Integer paddingTop,
            Integer paddingBottom,
            Integer paddingLeft,
            Integer paddingRight
    ) {
        this.content = content;
        this.alignment = alignment;
        this.fontSize = fontSize;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
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
    public Integer getFontSize() {
        return fontSize;
    }

    @Override
    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public Integer getPaddingTop() {
        return paddingTop;
    }

    @Override
    public void setPaddingTop(Integer paddingTop) {
        this.paddingTop = paddingTop;
    }

    @Override
    public Integer getPaddingBottom() {
        return paddingBottom;
    }

    @Override
    public void setPaddingBottom(Integer paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    @Override
    public Integer getPaddingLeft() {
        return paddingLeft;
    }

    @Override
    public void setPaddingLeft(Integer paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    @Override
    public Integer getPaddingRight() {
        return paddingRight;
    }

    @Override
    public void setPaddingRight(Integer paddingRight) {
        this.paddingRight = paddingRight;
    }
}
