package com.fancyinnovations.fancycore.uis.common;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class LocationUIData {

    public static final BuilderCodec<LocationUIData> CODEC = BuilderCodec.builder(LocationUIData.class, LocationUIData::new)
            .addField(
                    new KeyedCodec<>("LocationName", Codec.STRING),
                    LocationUIData::setLocationName,
                    LocationUIData::getLocationName
            )
            .build();

    private String locationName;

    public LocationUIData() {
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
