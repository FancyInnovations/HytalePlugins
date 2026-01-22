package com.fancyinnovations.fancycore.uis;

import com.hypixel.hytale.codec.builder.BuilderCodec;

public record EmptyUIData() {

    public static final BuilderCodec<EmptyUIData> CODEC = BuilderCodec.builder(EmptyUIData.class, EmptyUIData::new).build();
}
