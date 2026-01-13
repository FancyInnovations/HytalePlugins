package com.fancyinnovations.fancycore.player.storage.json;

import com.fancyinnovations.fancycore.api.player.FancyPlayerData;
import com.fancyinnovations.fancycore.api.player.FancyPlayerStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.player.FancyPlayerDataImpl;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class FancyPlayerJsonStorage implements FancyPlayerStorage {

    private static final String DATA_DIR_PATH = "mods/FancyCore/data/players";
    private final JDB jdb;

    public FancyPlayerJsonStorage() {
        this.jdb = new JDB(DATA_DIR_PATH);
    }

    @Override
    public void savePlayer(FancyPlayerData player) {
        if (!(player instanceof FancyPlayerDataImpl fpImpl)) {
            FancyCorePlugin.get().getFancyLogger().warn("Only real player objects can be saved");
            return;
        }

        JsonFancyPlayer jsonFancyPlayer = JsonFancyPlayer.from(fpImpl);
        try {
            jdb.set(fpImpl.getUUID().toString(), jsonFancyPlayer, "/by-username/" + fpImpl.getUsername());
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to save FancyPlayer",
                    StringProperty.of("uuid", jsonFancyPlayer.uuid()),
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public FancyPlayerData loadPlayer(UUID uuid) {
        try {
            JsonFancyPlayer jsonFancyPlayer = jdb.get(uuid.toString(), JsonFancyPlayer.class);
            return jsonFancyPlayer.toFancyPlayer();
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load FancyPlayer by UUID",
                    StringProperty.of("uuid", uuid.toString()),
                    ThrowableProperty.of(e)
            );
        }
        return null;
    }

    @Override
    public FancyPlayerData loadPlayerByUsername(String username) {
        try {
            String uuidStr = jdb.get("/by-username/" + username, String.class);
            if (uuidStr == null) {
                return null;
            }
            return loadPlayer(UUID.fromString(uuidStr));
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load FancyPlayer by username",
                    StringProperty.of("username", username),
                    ThrowableProperty.of(e)
            );
        }
        return null;
    }

    @Override
    public List<FancyPlayerData> loadAllPlayers() {
        try {
            List<JsonFancyPlayer> all = jdb.getAll("", JsonFancyPlayer.class);
            return all.stream()
                    .map(JsonFancyPlayer::toFancyPlayer)
                    .map(fpImpl -> (FancyPlayerData) fpImpl)
                    .toList();
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load all FancyPlayers",
                    ThrowableProperty.of(e)
            );
        }
        return List.of();
    }

    @Override
    public void deletePlayer(UUID uuid) {
        jdb.delete(uuid.toString());
    }

    @Override
    public int countPlayers() {
        return jdb.countDocuments("");
    }
}
