package com.fancyinnovations.fancycore.scoreboard.storage;

import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardLine;
import com.google.gson.annotations.SerializedName;

public record JsonScoreboardLine(
        String content,
        String alignment,
        @SerializedName("font_size") Integer fontSize,
        @SerializedName("padding_top") Integer paddingTop,
        @SerializedName("padding_bottom") Integer paddingBottom,
        @SerializedName("padding_left") Integer paddingLeft,
        @SerializedName("padding_right") Integer paddingRight
) {

    public static JsonScoreboardLine fromScoreboardLine(ScoreboardLine line) {
        return new JsonScoreboardLine(
                line.getContent(),
                line.getAlignment(),
                line.getFontSize(),
                line.getPaddingTop(),
                line.getPaddingBottom(),
                line.getPaddingLeft(),
                line.getPaddingRight()
        );
    }

    public ScoreboardLine toScoreboardLine() {
        return new com.fancyinnovations.fancycore.scoreboard.ScoreboardLineImpl(
                content,
                alignment,
                fontSize,
                paddingTop,
                paddingBottom,
                paddingLeft,
                paddingRight
        );
    }

}
