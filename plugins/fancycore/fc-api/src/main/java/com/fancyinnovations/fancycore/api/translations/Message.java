package com.fancyinnovations.fancycore.api.translations;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;

/**
 * Represents a translatable message with placeholder support.
 */
public interface Message {

    /**
     * Replace a placeholder with a value.
     * Supports both {placeholder} and %placeholder% formats.
     *
     * @param placeholder the placeholder name (without braces or percent signs)
     * @param replacement the value to replace with
     * @return this Message for chaining
     */
    Message replace(String placeholder, String replacement);

    /**
     * Send this message to a player.
     *
     * @param player the player to send to
     */
    void sendTo(FancyPlayer player);

    /**
     * Get the message key.
     *
     * @return the key
     */
    String getKey();

    /**
     * Get the raw (untranslated) message.
     *
     * @return the raw message
     */
    String getRawMessage();

    /**
     * Get the parsed message with all replacements applied.
     *
     * @return the parsed message
     */
    String getParsedMessage();
}
