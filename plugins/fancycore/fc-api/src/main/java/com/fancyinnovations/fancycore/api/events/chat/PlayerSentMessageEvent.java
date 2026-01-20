package com.fancyinnovations.fancycore.api.events.chat;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.discord.Message;
import com.fancyinnovations.fancycore.api.events.player.PlayerEvent;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.translations.MessageKey;

import java.util.List;

/**
 * Event fired when a player sends a message in a chat room.
 */
public class PlayerSentMessageEvent extends PlayerEvent {

    private final ChatRoom chatRoom;
    private final String rawMessage;
    private final String parsedMessage;

    public PlayerSentMessageEvent(FancyPlayer player, ChatRoom chatRoom, String rawMessage, String parsedMessage) {
        super(player);
        this.chatRoom = chatRoom;
        this.rawMessage = rawMessage;
        this.parsedMessage = parsedMessage;
    }

    /**
     * Gets the chat room where the message was sent.
     *
     * @return the chat room.
     */
    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    /**
     * Gets the raw message sent by the player.
     *
     * @return the raw message.
     */
    public String getRawMessage() {
        return rawMessage;
    }

    /**
     * Gets the parsed message after processing.
     *
     * @return the parsed message.
     */
    public String getParsedMessage() {
        return parsedMessage;
    }

    @Override
    public Message getDiscordMessage() {
        if (!chatRoom.getName().equals(FancyCore.get().getConfig().getDefaultChatroom())) {
            return null;
        }

        String message = FancyCore.get().getTranslationService()
                .getMessage(MessageKey.EVENT_CHAT_SENT)
                .replace("player", player.getData().getUsername())
                .replace("message", rawMessage)
                .replace("chat_room", chatRoom.getName())
                .getParsedMessage();

        return new Message(message, List.of());
    }
}
