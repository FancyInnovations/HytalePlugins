package com.fancyinnovations.fancycore.api.teleport;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface TeleportRequestService {

    static TeleportRequestService get() {
        return FancyCore.get().getTeleportRequestService();
    }

    /**
     * Sends a teleport request from sender to target.
     * @param sender The player sending the request
     * @param target The player receiving the request
     * @return true if the request was sent successfully, false if a request already exists
     */
    boolean sendRequest(FancyPlayer sender, FancyPlayer target);

    /**
     * Gets the pending request from a specific sender to the target.
     * @param target The player who received the request
     * @param sender The player who sent the request
     * @return The sender's UUID if a request exists, null otherwise
     */
    @Nullable UUID getRequest(FancyPlayer target, FancyPlayer sender);

    /**
     * Gets the first pending request for the target player.
     * @param target The player who received the request
     * @return The sender's UUID if a request exists, null otherwise
     */
    @Nullable UUID getFirstRequest(FancyPlayer target);

    /**
     * Removes a teleport request.
     * @param target The player who received the request
     * @param sender The player who sent the request
     * @return true if a request was removed, false otherwise
     */
    boolean removeRequest(FancyPlayer target, FancyPlayer sender);

    /**
     * Removes all requests for a target player.
     * @param target The player whose requests should be removed
     */
    void removeAllRequests(FancyPlayer target);
}
