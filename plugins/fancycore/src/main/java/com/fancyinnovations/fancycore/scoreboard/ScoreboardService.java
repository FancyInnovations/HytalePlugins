package com.fancyinnovations.fancycore.scoreboard;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.hypixel.hytale.server.core.entity.entities.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ScoreboardService {

    private final Map<FancyPlayer, ScoreboardUI> playerScoreboards;

    public ScoreboardService() {
        this.playerScoreboards = new ConcurrentHashMap<>();

        FancyCore.get().getThreadPool().scheduleWithFixedDelay(this::update, 0L, 500L, TimeUnit.MILLISECONDS);
    }

    public void registerScoreboard(FancyPlayer fancyPlayer, Player player) {
        List<ScoreboardLine> lines = new ArrayList<>();
        lines.add(new ScoreboardLine(
                "&6&lFancyCore",
                "Center",
                30,
                20,
                5,
                10,
                null
        ));
        lines.add(new ScoreboardLine(
                "&6Player: &e%player_name%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        lines.add(new ScoreboardLine(
                "&6Group: &e%player_group_prefix%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        lines.add(new ScoreboardLine(
                "&6Money: &2$&a%player_balance%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        lines.add(new ScoreboardLine(
                "&6Playtime: &e%player_playtime%",
                null,
                null,
                null,
                null,
                null,
                null
        ));

        ScoreboardPage scoreboardPage = new ScoreboardPage(
                "default",
                "Right",
                250,
                400,
                200,
                lines
        );

        ScoreboardUI scoreboardUI = new ScoreboardUI(fancyPlayer, scoreboardPage);
        playerScoreboards.put(fancyPlayer, scoreboardUI);

        player.getHudManager().setCustomHud(fancyPlayer.getPlayer(), new ScoreboardUI(fancyPlayer, scoreboardPage));
    }

    public void unregisterScoreboard(FancyPlayer fancyPlayer, Player player) {
        playerScoreboards.remove(fancyPlayer);
        player.getHudManager().resetHud(fancyPlayer.getPlayer());
    }

    private void update() {
        for (Map.Entry<FancyPlayer, ScoreboardUI> entry : playerScoreboards.entrySet()) {
            FancyPlayer fancyPlayer = entry.getKey();
            ScoreboardUI scoreboardUI = entry.getValue();
            scoreboardUI.refreshUI();
        }
    }

}
