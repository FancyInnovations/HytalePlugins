package com.fancyinnovations.fancycore.api.translations;

/**
 * Service for retrieving translated messages.
 */
public interface TranslationService {

    /**
     * Get a translated message by its key.
     *
     * @param key the message key
     * @return a Message object that can be used for placeholder replacement
     */
    Message getMessage(String key);

    /**
     * Get a translated message by its MessageKey enum.
     *
     * @param key the MessageKey enum value
     * @return a Message object that can be used for placeholder replacement
     */
    Message getMessage(MessageKey key);

    /**
     * Get the raw message string for a key.
     *
     * @param key the message key
     * @return the raw message string
     */
    String getRaw(String key);

    /**
     * Get the raw message string for a MessageKey.
     *
     * @param key the MessageKey enum value
     * @return the raw message string
     */
    String getRaw(MessageKey key);
}
