package com.fancyinnovations.fancycore.translations;

import com.fancyinnovations.fancycore.api.translations.MessageKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationService implements com.fancyinnovations.fancycore.api.translations.TranslationService {

    private static final String LANG_DIR = com.fancyinnovations.fancycore.config.FancyCorePaths.LANG_DIR;
    private static final String DEFAULT_LANG = "en_us";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, String> messages;
    private final Map<String, String> defaults;

    public TranslationService(Map<String, String> messages) {
        this.messages = new ConcurrentHashMap<>(messages);
        this.defaults = new LinkedHashMap<>();
        loadDefaults();
    }

    public TranslationService() {
        this.messages = new ConcurrentHashMap<>();
        this.defaults = new LinkedHashMap<>();
        loadDefaults();
        loadFromFile(DEFAULT_LANG);
    }

    /**
     * Load default messages that are used as fallbacks if a key is missing.
     */
    private void loadDefaults() {
        // Player events
        defaults.put(MessageKey.EVENT_PLAYER_JOINED.getKey(), "Player {player} has joined the server.");
        defaults.put(MessageKey.EVENT_PLAYER_LEFT.getKey(), "Player {player} has left the server.");
        defaults.put(MessageKey.EVENT_PLAYER_REPORTED.getKey(), "{player} has been reported by {reporter}. Reason: {reason}");
        defaults.put(MessageKey.EVENT_PLAYER_PUNISHED.getKey(), "Player {player} was punished. Type: {type}, Reason: {reason}");
        defaults.put(MessageKey.EVENT_PLAYER_PUNISHED_WARNING.getKey(), "{player} has been warned. Reason: {reason}, Issued by: {issued_by}");
        defaults.put(MessageKey.EVENT_PLAYER_PUNISHED_MUTE.getKey(), "{player} has been muted. Reason: {reason}, Issued by: {issued_by}, Duration: {duration}");
        defaults.put(MessageKey.EVENT_PLAYER_PUNISHED_KICK.getKey(), "{player} has been kicked. Reason: {reason}, Issued by: {issued_by}");
        defaults.put(MessageKey.EVENT_PLAYER_PUNISHED_BAN.getKey(), "{player} has been banned. Reason: {reason}, Issued by: {issued_by}, Duration: {duration}");
        defaults.put(MessageKey.EVENT_PLAYER_GENERIC.getKey(), "A player event occurred for {player}.");

        // Chat events
        defaults.put(MessageKey.EVENT_CHAT_SENT.getKey(), "{player}: {message}");

        // Server events
        defaults.put(MessageKey.EVENT_SERVER_STARTED.getKey(), "Server has started!");
        defaults.put(MessageKey.EVENT_SERVER_STOPPED.getKey(), "Server has stopped!");

        // Generic event
        defaults.put(MessageKey.EVENT_GENERIC.getKey(), "An event of type {event_type} was fired.");

        // Chat room messages
        defaults.put(MessageKey.CHAT_ROOM_MUTED.getKey(), "Chat is currently muted.");
        defaults.put(MessageKey.CHAT_ROOM_COOLDOWN.getKey(), "You must wait {time} before sending another message.");
        defaults.put(MessageKey.CHAT_ROOM_CLEARED.getKey(), "Chat has been cleared.");

        // Ban messages
        defaults.put(MessageKey.BAN_PERMANENT.getKey(), "You are banned from this server.\nReason: {reason}");
        defaults.put(MessageKey.BAN_TEMPORARY.getKey(), "You are banned from this server.\nReason: {reason}\nRemaining duration: {duration}");

        // Discord embed titles
        defaults.put(MessageKey.DISCORD_EVENT_FIRED.getKey(), "Event fired");
        defaults.put(MessageKey.DISCORD_PLAYER_EVENT_FIRED.getKey(), "Player event fired");
        defaults.put(MessageKey.DISCORD_PLAYER_REPORTED.getKey(), "Player reported");
        defaults.put(MessageKey.DISCORD_PLAYER_WARNED.getKey(), "Player warned");
        defaults.put(MessageKey.DISCORD_PLAYER_MUTED.getKey(), "Player muted");
        defaults.put(MessageKey.DISCORD_PLAYER_KICKED.getKey(), "Player kicked");
        defaults.put(MessageKey.DISCORD_PLAYER_BANNED.getKey(), "Player banned");
    }

    /**
     * Load messages from a language file (e.g., "en_us" loads from mods/FancyCore/lang/en_us.json).
     * If the file doesn't exist, it will be created with default values.
     *
     * @param langCode the language code (e.g., "en_us")
     */
    public void loadFromFile(String langCode) {
        Path langDir = Path.of(LANG_DIR);
        Path langFile = langDir.resolve(langCode + ".json");

        try {
            // Create directory if it doesn't exist
            if (!Files.exists(langDir)) {
                Files.createDirectories(langDir);
            }

            // If file doesn't exist, create it with defaults
            if (!Files.exists(langFile)) {
                saveToFile(langCode);
                messages.putAll(defaults);
                return;
            }

            // Read and parse the JSON file
            String content = Files.readString(langFile, StandardCharsets.UTF_8);
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> loaded = GSON.fromJson(content, type);

            if (loaded != null) {
                messages.putAll(loaded);
            }

            // Add any missing defaults and save updated file
            boolean needsSave = false;
            for (Map.Entry<String, String> entry : defaults.entrySet()) {
                if (!messages.containsKey(entry.getKey())) {
                    messages.put(entry.getKey(), entry.getValue());
                    needsSave = true;
                }
            }

            if (needsSave) {
                saveToFile(langCode);
            }

        } catch (IOException e) {
            // Fall back to defaults if file loading fails
            messages.putAll(defaults);
        }
    }

    /**
     * Save current messages to a language file.
     *
     * @param langCode the language code (e.g., "en_us")
     */
    public void saveToFile(String langCode) {
        Path langDir = Path.of(LANG_DIR);
        Path langFile = langDir.resolve(langCode + ".json");

        try {
            if (!Files.exists(langDir)) {
                Files.createDirectories(langDir);
            }

            // Merge defaults with current messages, preserving order
            Map<String, String> toSave = new LinkedHashMap<>(defaults);
            toSave.putAll(messages);

            String json = GSON.toJson(toSave);
            Files.writeString(langFile, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Silently fail - logging not available at this point
        }
    }

    /**
     * Add or update a message.
     *
     * @param message the Message object to add
     * @return this TranslationService for chaining
     */
    public TranslationService addMessage(Message message) {
        this.messages.put(message.getKey(), message.getRawMessage());
        return this;
    }

    /**
     * Add or update a message by key and value.
     *
     * @param key     the message key
     * @param message the message text
     * @return this TranslationService for chaining
     */
    public TranslationService addMessage(String key, String message) {
        this.messages.put(key, message);
        return this;
    }

    @Override
    public com.fancyinnovations.fancycore.api.translations.Message getMessage(String key) {
        String message = this.messages.get(key);
        if (message == null) {
            message = this.defaults.get(key);
        }
        if (message == null) {
            return new Message(key, "Missing translation for key: " + key);
        }

        return new Message(key, message);
    }

    @Override
    public com.fancyinnovations.fancycore.api.translations.Message getMessage(MessageKey key) {
        return getMessage(key.getKey());
    }

    @Override
    public String getRaw(String key) {
        return getMessage(key).getRawMessage();
    }

    @Override
    public String getRaw(MessageKey key) {
        return getMessage(key).getRawMessage();
    }
}
