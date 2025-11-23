package com.fancyinnovations.fancycore.api.events.player;

import com.fancyinnovations.fancycore.api.events.FancyEvent;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;

public abstract class PlayerEvent extends FancyEvent {

    private  final FancyPlayer player;

    public PlayerEvent(FancyPlayer player) {
        super();
        this.player = player;
    }

    public FancyPlayer getPlayer() {
        return player;
    }
}
