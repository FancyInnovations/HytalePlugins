package com.fancyinnovations.fancycore.api.moderation;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;

public interface PlayerReport {

    String id();

    long reportedAt();

    FancyPlayer reportedPlayer();

    FancyPlayer reportingPlayer();

    String reason();

    boolean isResolved();

    long resolvedAt();

}
