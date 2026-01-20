package com.fancyinnovations.fancycore.config;

/**
 * Centralized constants for all FancyCore file and directory paths.
 */
public final class FancyCorePaths {

    private FancyCorePaths() {
        // Utility class
    }

    /**
     * Base directory for all FancyCore data.
     */
    public static final String BASE_DIR = "mods/FancyCore";

    /**
     * Configuration file path.
     */
    public static final String CONFIG_FILE = BASE_DIR + "/config.json";

    /**
     * Logs directory.
     */
    public static final String LOGS_DIR = BASE_DIR + "/logs";

    /**
     * Language files directory.
     */
    public static final String LANG_DIR = BASE_DIR + "/lang";

    /**
     * Base data directory.
     */
    public static final String DATA_DIR = BASE_DIR + "/data";

    /**
     * Players data directory.
     */
    public static final String PLAYERS_DATA_DIR = DATA_DIR + "/players";

    /**
     * Groups/permissions data directory.
     */
    public static final String GROUPS_DATA_DIR = DATA_DIR + "/groups";

    /**
     * Chat rooms data directory.
     */
    public static final String CHATROOMS_DATA_DIR = DATA_DIR + "/chatrooms";

    /**
     * Currencies data directory.
     */
    public static final String CURRENCIES_DATA_DIR = DATA_DIR + "/currencies";

    /**
     * Kits data directory.
     */
    public static final String KITS_DATA_DIR = DATA_DIR + "/kits";

    /**
     * Backpacks data directory.
     */
    public static final String BACKPACKS_DATA_DIR = DATA_DIR + "/backpacks";

    /**
     * Warps data directory.
     */
    public static final String WARPS_DATA_DIR = DATA_DIR + "/warps";

    /**
     * Scoreboards data directory.
     */
    public static final String SCOREBOARDS_DATA_DIR = DATA_DIR + "/scoreboards";

    /**
     * Punishments data directory.
     */
    public static final String PUNISHMENTS_DATA_DIR = DATA_DIR + "/punishments";

    /**
     * Reports data directory.
     */
    public static final String REPORTS_DATA_DIR = DATA_DIR + "/reports";

    /**
     * Plugin JAR file path.
     */
    public static final String PLUGIN_JAR = "mods/FancyCore.jar";

    /**
     * Get the log file path for a specific date.
     *
     * @param date the date string (e.g., "2024-01-15")
     * @return the full path to the log file
     */
    public static String getLogFile(String date) {
        return LOGS_DIR + "/FC-logs-" + date + ".txt";
    }

    /**
     * Get the language file path for a specific language code.
     *
     * @param langCode the language code (e.g., "en_us")
     * @return the full path to the language file
     */
    public static String getLangFile(String langCode) {
        return LANG_DIR + "/" + langCode + ".json";
    }
}
