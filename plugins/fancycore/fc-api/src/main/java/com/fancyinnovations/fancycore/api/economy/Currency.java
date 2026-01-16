package com.fancyinnovations.fancycore.api.economy;

public record Currency(
    String name,
    String symbol,
    int decimalPlaces,
    String server
) {
    
}
