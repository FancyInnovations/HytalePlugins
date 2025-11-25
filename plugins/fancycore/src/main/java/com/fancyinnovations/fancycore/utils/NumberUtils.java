package com.fancyinnovations.fancycore.utils;

import java.text.DecimalFormat;

public class NumberUtils {

    private static final String[] NUMBER_SUFFIXES = {
        "", "k", "m", "b", "t", "q", "Q"
    };

    private static final DecimalFormat FORMAT_TWO_DECIMALS = new DecimalFormat("#.##");

    public static String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return String.valueOf(value);
        }

        final double absoluteValue = Math.abs(value);

        int suffixIndex = 0;
        if (absoluteValue >= 1000) {
            suffixIndex = (int) (Math.log10(absoluteValue) / 3);
            suffixIndex = Math.min(suffixIndex, NUMBER_SUFFIXES.length - 1);
        }

        final double scaledValue = value / Math.pow(1000, suffixIndex);
        final String formattedValue = FORMAT_TWO_DECIMALS.format(scaledValue);

        return formattedValue + NUMBER_SUFFIXES[suffixIndex];
    }

}
