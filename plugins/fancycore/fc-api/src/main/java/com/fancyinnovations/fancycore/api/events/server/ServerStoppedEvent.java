package com.fancyinnovations.fancycore.api.events.server;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.discord.Message;
import com.fancyinnovations.fancycore.api.events.FancyEvent;
import com.fancyinnovations.fancycore.api.translations.MessageKey;

import java.util.List;

public class ServerStoppedEvent extends FancyEvent {

    public ServerStoppedEvent() {
        super();
    }

    @Override
    public Message getDiscordMessage() {
        String message = FancyCore.get().getTranslationService().getRaw(MessageKey.EVENT_SERVER_STOPPED);
        return new Message(message, List.of());
    }

}
