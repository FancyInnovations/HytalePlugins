package com.fancyinnovations.fancycore.uis.inventory;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class KitUIData {

    public static final BuilderCodec<KitUIData> CODEC = BuilderCodec.builder(KitUIData.class, KitUIData::new)
            .addField(
                    new KeyedCodec<>("KitName", Codec.STRING),
                    KitUIData::setKitName,
                    KitUIData::getKitName
            )
            .build();

    private String kitName;

    public KitUIData() {
    }

    public String getKitName() {
        return kitName;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }
}
