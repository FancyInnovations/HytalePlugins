package com.fancyinnovations.fancycore.config;

import com.fancyinnovations.config.ConfigField;
import com.fancyinnovations.config.ConfigJSON;
import com.fancyinnovations.fancycore.api.FancyCoreConfig;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;

public class FancyCoreConfigImpl implements FancyCoreConfig {

    public static final String SERVER_NAME_PATH = "settings.server_name";
    public static final String LOG_LEVEL_PATH = "settings.logging.level";
    public static final String EVENT_DISCORD_WEBHOOK_URL_PATH = "settings.events.discord_webhook_url";
    public static final String EVENT_DISCORD_NOTIFICATIONS = "settings.events.notifications_enabled";
    public static final String PRIMARY_CURRENCY_NAME_PATH = "settings.economy.primary_currency";
    public static final String CHAT_FORMAT_PATH = "settings.chat.format";
    public static final String DEFAULT_CHATROOM_PATH = "settings.chat.default_chatroom";
    public static final String PRIVATE_MESSAGES_FORMAT_PATH = "settings.chat.private_messages_format";
    public static final String JOIN_MESSAGE_PATH = "settings.join_message";
    public static final String FIRST_JOIN_MESSAGE_PATH = "settings.first_join_message";
    public static final String LEAVE_MESSAGE_PATH = "settings.leave_message";
    public static final String SHOULD_JOIN_AT_SPAWN_PATH = "settings.join_at_spawn";
    public static final String DEFAULT_GROUP_NAME_PATH = "settings.default_group_name";
    public static final String FIRST_JOIN_KIT_PATH = "settings.first_join_kit";
    public static final String DEFAULT_SCOREBOARD_PAGE_PATH = "settings.scoreboard.default_page";
    public static final String SCOREBOARD_REFRESH_INTERVAL_PATH = "settings.scoreboard.refresh_interval";
    public static final String DISABLE_ANALYTICS_PATH = "settings.disable_analytics";

    public static final String DISABLE_PERMISSION_PROVIDER_PATH = "experimental_features.disable_permission_provider";

    private static final String CONFIG_FILE_PATH = FancyCorePaths.CONFIG_FILE;
    private ConfigJSON config;

    public void init() {
        config = new ConfigJSON(FancyCorePlugin.get().getFancyLogger(), CONFIG_FILE_PATH);

        config.addField(new ConfigField<>(
                SERVER_NAME_PATH,
                "The name of the server (needed if you use multiple servers with FancyCore).",
                false,
                "hytale-server",
                false,
                String.class
        ));

        config.addField(new ConfigField<>(
                LOG_LEVEL_PATH,
                "The log level for the plugin (DEBUG, INFO, WARN, ERROR).",
                false,
                "INFO",
                false,
                String.class
        ));

        config.addField(new ConfigField<>(
                EVENT_DISCORD_WEBHOOK_URL_PATH,
                "The Discord webhook URL for event notifications. Leave empty to disable all event notifications.",
                false,
                "",
                false,
                String.class
        ));

        config.addField(new ConfigField<>(
                EVENT_DISCORD_NOTIFICATIONS,
                "Enable Discord notifications for events.",
                false,
                new String[] {"PlayerJoinedEvent", "PlayerLeftEvent", "PlayerSentMessageEvent"},
                false,
                String[].class
        ));

        config.addField(new ConfigField<>(
                PRIMARY_CURRENCY_NAME_PATH,
                "The name of the primary currency used in the economy system.",
                false,
                "Money",
                false,
                String.class
        ));

        config.addField(
                new ConfigField<>(
                        CHAT_FORMAT_PATH,
                        "The default chat format for messages.",
                        false,
                        "&6[&e%chat_room%&6]&r %player_group_prefix% %player_nickname%&7: &r%message%",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        DEFAULT_CHATROOM_PATH,
                        "The name of the default chatroom players join upon connecting.",
                        false,
                        "global",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        PRIVATE_MESSAGES_FORMAT_PATH,
                        "The format for private messages between players.",
                        false,
                        "&e<&6&l%sender% &e-> &6&l%receiver%&e> &r%message%",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        JOIN_MESSAGE_PATH,
                        "The message displayed when a player joins the server.",
                        false,
                        "&6%player_name% &ehas joined the game.",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        FIRST_JOIN_MESSAGE_PATH,
                        "The message displayed when a player joins the server for the first time.",
                        false,
                        "&eWelcome &6%player_name% &eto the server for the first time!",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        LEAVE_MESSAGE_PATH,
                        "The message displayed when a player leaves the server.",
                        false,
                        "&6%player_name% &ehas left the game.",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        SHOULD_JOIN_AT_SPAWN_PATH,
                        "Determines if players should be teleported to spawn upon joining the server.",
                        false,
                        false,
                        false,
                        Boolean.class
                )
        );

        config.addField(
                new ConfigField<>(
                        DEFAULT_GROUP_NAME_PATH,
                        "The name of the default group assigned to new players.",
                        false,
                        "member",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        FIRST_JOIN_KIT_PATH,
                        "The name of the kit given to players upon their first join.",
                        false,
                        "starter",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        DEFAULT_SCOREBOARD_PAGE_PATH,
                        "The name of the default scoreboard page shown to players. Empty to disable.",
                        false,
                        "default",
                        false,
                        String.class
                )
        );

        config.addField(
                new ConfigField<>(
                        SCOREBOARD_REFRESH_INTERVAL_PATH,
                        "The interval (in milliseconds) at which the scoreboard refreshes for players. Lower values may impact performance.",
                        false,
                        1000,
                        false,
                        Integer.class
                )
        );

        config.addField(
                new ConfigField<>(
                        DISABLE_ANALYTICS_PATH,
                        "If true, FancyCore will not collect any anonymous usage metrics.",
                        false,
                        false,
                        false,
                        Boolean.class
                )
        );

        // Experimental Features

        config.addField(
                new ConfigField<>(
                        DISABLE_PERMISSION_PROVIDER_PATH,
                        "If true, FancyCore will not register its built-in permission provider.",
                        false,
                        false,
                        false,
                        Boolean.class
                )
        );

        config.reload();
    }

    @Override
    public void reload() {
        config.reload();
    }

    @Override
    public String getServerName() {
        return config.get(SERVER_NAME_PATH);
    }

    @Override
    public String getLogLevel() {
        return config.get(LOG_LEVEL_PATH);
    }

    @Override
    public String getEventDiscordWebhookUrl() {
        return config.get(EVENT_DISCORD_WEBHOOK_URL_PATH);
    }

    @Override
    public String[] getEventDiscordNotifications() {
        return config.get(EVENT_DISCORD_NOTIFICATIONS);
    }

    @Override
    public String primaryCurrencyName() {
        return config.get(PRIMARY_CURRENCY_NAME_PATH);
    }

    @Override
    public String getChatFormat() {
        return config.get(CHAT_FORMAT_PATH);
    }

    @Override
    public String getDefaultChatroom() {
        return config.get(DEFAULT_CHATROOM_PATH);
    }

    @Override
    public String getPrivateMessageFormat() {
        return config.get(PRIVATE_MESSAGES_FORMAT_PATH);
    }

    @Override
    public String getJoinMessage() {
        return config.get(JOIN_MESSAGE_PATH);
    }

    @Override
    public String getFirstJoinMessage() {
        return config.get(FIRST_JOIN_MESSAGE_PATH);
    }

    @Override
    public String getLeaveMessage() {
        return config.get(LEAVE_MESSAGE_PATH);
    }

    @Override
    public boolean shouldJoinAtSpawn() {
        return config.get(SHOULD_JOIN_AT_SPAWN_PATH);
    }

    @Override
    public String getDefaultGroupName() {
        return config.get(DEFAULT_GROUP_NAME_PATH);
    }

    @Override
    public String getFirstJoinKitName() {
        return config.get(FIRST_JOIN_KIT_PATH);
    }

    @Override
    public String getDefaultScoreboardPageName() {
        return config.get(DEFAULT_SCOREBOARD_PAGE_PATH);
    }

    @Override
    public int getScoreboardRefreshInterval() {
        return config.get(SCOREBOARD_REFRESH_INTERVAL_PATH);
    }

    @Override
    public boolean isAnalyticsDisabled() {
        return config.get(DISABLE_ANALYTICS_PATH);
    }

    @Override
    public boolean disablePermissionProvider() {
        return config.get(DISABLE_PERMISSION_PROVIDER_PATH);
    }
}
