package com.fancyinnovations.fancycore.translations;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;

public class Message implements com.fancyinnovations.fancycore.api.translations.Message {

    private final String key;
    private final String raw;
    private String parsed;

    public Message(String key, String message) {
        this.key = key;
        this.raw = message;
        this.parsed = message;
    }

    @Override
    public Message replace(String placeholder, String replacement) {
        this.parsed = this.parsed
                .replace("{" + placeholder + "}", replacement)
                .replace("%" + placeholder + "%", replacement);

        return this;
    }

    @Override
    public void sendTo(FancyPlayer player) {
        if (!player.isOnline()) {
            return;
        }

        player.sendMessage(this.parsed);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getRawMessage() {
        return raw;
    }

    @Override
    public String getParsedMessage() {
        return parsed;
    }
}
