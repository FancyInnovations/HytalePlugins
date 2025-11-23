package com.fancyinnovations.fancycore.api.events.punishments;

import com.fancyinnovations.fancycore.api.events.player.PlayerEvent;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.punishments.Punishment;

public class PlayerPunishedEvent extends PlayerEvent {

    private final Punishment punishment;

    public PlayerPunishedEvent(FancyPlayer player, Punishment punishment) {
        super(player);
        this.punishment = punishment;
    }

    public Punishment getPunishment() {
        return punishment;
    }
}
