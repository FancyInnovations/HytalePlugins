package com.fancyinnovations.fancycore.api;

public interface FancyCoreConfig {

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

    void reload();

}
