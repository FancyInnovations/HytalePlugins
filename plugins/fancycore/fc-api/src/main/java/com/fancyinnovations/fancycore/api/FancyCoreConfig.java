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

    // Spawn location
    String getSpawnWorld();
    Double getSpawnX();
    Double getSpawnY();
    Double getSpawnZ();
    Double getSpawnYaw();
    Double getSpawnPitch();
    void setSpawnLocation(String world, double x, double y, double z, double yaw, double pitch);

    void reload();

}
