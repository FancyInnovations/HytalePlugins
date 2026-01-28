package com.fancyinnovations.hytaleutils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final DecimalFormat SECONDS_FORMAT = new DecimalFormat("#.##");

    public static String formatTime(long millis) {
        double seconds = millis / 1000d;
        long minutes = (long) (seconds / 60);
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder formattedTime = new StringBuilder();
        if (days > 0) {
            formattedTime.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            formattedTime.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            formattedTime.append(minutes).append("m ");
        }
        formattedTime.append(SECONDS_FORMAT.format(seconds)).append("s");

        return formattedTime.toString().trim();
    }

    public static String formatDate(long timestamp) {
        Date date = new Date(timestamp);
        return DATE_FORMAT.format(date);
    }

}
