package com.fancyinnovations.fancycore.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilsTest {

    @Test
    void formatNumber_shouldHandleSmallNumbersWithoutSuffix() {
        assertEquals("0", NumberUtils.formatNumber(0));
        assertEquals("12.34", NumberUtils.formatNumber(12.34));
        assertEquals("-56.78", NumberUtils.formatNumber(-56.78));
    }

    @Test
    void formatNumber_shouldAddKSuffix_forThousands() {
        assertEquals("1k", NumberUtils.formatNumber(1_000));
        assertEquals("1.5k", NumberUtils.formatNumber(1_500));
        assertEquals("-2k", NumberUtils.formatNumber(-2_000));
    }

    @Test
    void formatNumber_shouldUseAppropriateSuffix_forLargeNumbers() {
        assertEquals("12.34k", NumberUtils.formatNumber(12_340));
        assertEquals("5.67m", NumberUtils.formatNumber(5_670_000));
        assertEquals("3.21b", NumberUtils.formatNumber(3_210_000_000d));
    }

    @Test
    void formatNumber_shouldNotSuffix_whenValueJustBelowThreshold() {
        assertEquals("999.99", NumberUtils.formatNumber(999.99));
    }

    @Test
    void formatNumber_shouldHandleDecimalPrecision() {
        assertEquals("1.23k", NumberUtils.formatNumber(1_234));
        assertEquals("1.23k", NumberUtils.formatNumber(1_234.0));
    }

    @Test
    void formatNumber_shouldHandleSpecialValues_NaN_and_Infinite() {
        assertEquals(String.valueOf(Double.NaN), NumberUtils.formatNumber(Double.NaN));
        assertEquals(String.valueOf(Double.POSITIVE_INFINITY), NumberUtils.formatNumber(Double.POSITIVE_INFINITY));
        assertEquals(String.valueOf(Double.NEGATIVE_INFINITY), NumberUtils.formatNumber(Double.NEGATIVE_INFINITY));
    }
}
