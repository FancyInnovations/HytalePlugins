package com.fancyinnovations.fancycore.api.events.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.punishments.PlayerReport;

public class PlayerReportedEvent extends PlayerEvent {

    private final PlayerReport report;

    public PlayerReportedEvent(FancyPlayer player, PlayerReport report) {
        super(player);
        this.report = report;
    }

    public PlayerReport getReport() {
        return report;
    }
}
