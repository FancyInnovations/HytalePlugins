package com.fancyinnovations.fancycore.chat.storage.json;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.chat.ChatStorage;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.util.List;

public class ChatJsonStorage implements ChatStorage {

    private static final String DATA_DIR_PATH = "mods/FancyCore/data/chatrooms";
    private final JDB db;

    public ChatJsonStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    @Override
    public void setChatRoom(ChatRoom room) {
        try {
            JsonChatRoom jsonRoom = JsonChatRoom.from(room);
            db.set(room.getName(), jsonRoom);
        } catch (Exception e) {
            FancyCore.get().getFancyLogger().error(
                    "Failed to store ChatRoom",
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public ChatRoom getChatRoom(String room) {
        try {
            JsonChatRoom jsonRoom = db.get(room, JsonChatRoom.class);
            if (jsonRoom != null) {
                return jsonRoom.toChatRoom();
            }
        } catch (Exception e) {
            FancyCore.get().getFancyLogger().error(
                    "Failed to load ChatRoom",
                    ThrowableProperty.of(e)
            );
        }

        return null;
    }

    @Override
    public void deleteChatRoom(String room) {
        db.delete(room);
    }

    @Override
    public List<ChatRoom> getAllChatRooms() {
        try {
            List<JsonChatRoom> jsonRooms = db.getAll("", JsonChatRoom.class);
            return jsonRooms.stream()
                    .map(JsonChatRoom::toChatRoom)
                    .toList();
        } catch (Exception e) {
            FancyCore.get().getFancyLogger().error(
                    "Failed to load all ChatRooms",
                    ThrowableProperty.of(e)
            );
        }

        return List.of();
    }
}
