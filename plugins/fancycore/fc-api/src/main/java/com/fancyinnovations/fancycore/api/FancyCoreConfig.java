package com.fancyinnovations.fancycore.api;

public interface FancyCoreConfig {

    // Settings

    String getServerName();

    String getLogLevel();

    String getEventDiscordWebhookUrl();

    String[] getEventDiscordNotifications();

    String primaryCurrencyName();

    String getChatFormat();

    String getDefaultChatroom();

    String getPrivateMessageFormat();

    String getJoinMessage();

    String getFirstJoinMessage();

    String getLeaveMessage();

    boolean shouldJoinAtSpawn();

    String getDefaultGroupName();

    String getFirstJoinKitName();

    String getDefaultScoreboardPageName();

    int getScoreboardRefreshInterval();

    boolean isAnalyticsDisabled();

    // Experimental features

    boolean disablePermissionSystem();

    boolean disableEconomySystem();

    boolean disableScoreboardSystem();

    void reload();

}
