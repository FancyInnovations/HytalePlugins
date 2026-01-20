package com.fancyinnovations.fancycore.api.translations;

/**
 * Enum containing all translation message keys used throughout FancyCore.
 */
public enum MessageKey {
    // Player events
    EVENT_PLAYER_JOINED("event.player.joined"),
    EVENT_PLAYER_LEFT("event.player.left"),
    EVENT_PLAYER_REPORTED("event.player.reported"),
    EVENT_PLAYER_PUNISHED("event.player.punished"),
    EVENT_PLAYER_PUNISHED_WARNING("event.player.punished.warning"),
    EVENT_PLAYER_PUNISHED_MUTE("event.player.punished.mute"),
    EVENT_PLAYER_PUNISHED_KICK("event.player.punished.kick"),
    EVENT_PLAYER_PUNISHED_BAN("event.player.punished.ban"),
    EVENT_PLAYER_GENERIC("event.player.generic"),

    // Chat events
    EVENT_CHAT_SENT("event.chat.sent"),

    // Server events
    EVENT_SERVER_STARTED("event.server.started"),
    EVENT_SERVER_STOPPED("event.server.stopped"),

    // Generic event
    EVENT_GENERIC("event.generic"),

    // Chat room messages
    CHAT_ROOM_MUTED("chat.room.muted"),
    CHAT_ROOM_COOLDOWN("chat.room.cooldown"),
    CHAT_ROOM_CLEARED("chat.room.cleared"),

    // Ban messages
    BAN_PERMANENT("ban.permanent"),
    BAN_TEMPORARY("ban.temporary"),

    // Discord embed titles
    DISCORD_EVENT_FIRED("discord.event.fired"),
    DISCORD_PLAYER_EVENT_FIRED("discord.player_event.fired"),
    DISCORD_PLAYER_REPORTED("discord.player.reported"),
    DISCORD_PLAYER_WARNED("discord.player.warned"),
    DISCORD_PLAYER_MUTED("discord.player.muted"),
    DISCORD_PLAYER_KICKED("discord.player.kicked"),
    DISCORD_PLAYER_BANNED("discord.player.banned");

    private final String key;

    MessageKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
