package com.fancyinnovations.fancycore.gui;

import com.fancyinnovations.uihelper.BaseGuiData;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import javax.annotation.Nullable;

/**
 * Data object for the moderation GUI.
 * Holds the target player name entered in the text field.
 */
public class ModerationGuiData extends BaseGuiData {

    public static final BuilderCodec<ModerationGuiData> CODEC = BaseGuiData.codec(ModerationGuiData.class, ModerationGuiData::new)
            .addField(new KeyedCodec<>("@PlayerName", Codec.STRING), (d, v) -> d.playerName = v, d -> d.playerName)
            .build();

    @Nullable
    public String playerName;

    public ModerationGuiData() {
    }
}

