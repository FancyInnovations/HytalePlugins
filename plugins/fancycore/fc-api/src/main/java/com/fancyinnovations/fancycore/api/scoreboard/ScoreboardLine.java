package com.fancyinnovations.fancycore.api.scoreboard;

public interface ScoreboardLine {

    String getContent();

    void setContent(String content);

    String getAlignment();

    void setAlignment(String alignment);

    Integer getFontSize();

    void setFontSize(Integer fontSize);

    Integer getPaddingTop();

    void setPaddingTop(Integer paddingTop);

    Integer getPaddingBottom();

    void setPaddingBottom(Integer paddingBottom);

    Integer getPaddingLeft();

    void setPaddingLeft(Integer paddingLeft);

    Integer getPaddingRight();

    void setPaddingRight(Integer paddingRight);

}
