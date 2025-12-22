package com.fancyinnovations.fancycore.api.player;

import com.fancyinnovations.fancycore.api.moderation.Punishment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper around the actual Player object from the Hytale API.
 * This interface provides methods to interact with the player.
 */
public interface FancyPlayer {

    /**
     * Gets the player's data.
     *
     * @return the player's data.
     */
    FancyPlayerData getData();

    /**
     * Gets the actual player object from the Hytale API.
     *
     * @return the player object, or null if the player is offline.
     */
    @Nullable FakeHytalePlayer getPlayer();

    /**
     * Sets the actual player object from the Hytale API.
     *
     * @param player the player object.
     */
    @ApiStatus.Internal
    void setPlayer(FakeHytalePlayer player);

    /**
     * Checks if the player has the specified permission (checks player permissions and group permissions).
     *
     * @param permission the permission to check.
     * @return true if the player has the permission, false otherwise.
     */
    boolean checkPermission(String permission);

    /**
     * Checks if the player is in the specified group.
     *
     * @param group the group UUID to check.
     * @return true if the player is in the group, false otherwise.
     */
    boolean isInGroup(String group);

    /**
     * Checks if the player is currently muted.
     *
     * @return the mute punishment if the player is muted, null otherwise.
     */
    Punishment isMuted();

    /**
     * Checks if the player is currently banned.
     *
     * @return the ban punishment if the player is banned, null otherwise.
     */
    Punishment isBanned();

    /**
     * Checks if the player is currently online.
     *
     * @return true if the player is online, false otherwise.
     */
    boolean isOnline();

    /**
     * Sends a message to the player.
     *
     * @param message the message to send.
     */
    void sendMessage(String message);

}
