package com.fancyinnovations.fancycore.scoreboard.service;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardService;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.scoreboard.ScoreboardUI;
import com.hypixel.hytale.server.core.entity.entities.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ScoreboardServiceImpl implements ScoreboardService {

    private final Map<FancyPlayer, ScoreboardUI> playerScoreboards;
    private final Map<String, ScoreboardPage> cache;
    private final ScoreboardStorage storage;

    public ScoreboardServiceImpl(ScoreboardStorage storage) {
        this.playerScoreboards = new ConcurrentHashMap<>();
        this.cache = new ConcurrentHashMap<>();
        this.storage = storage;

        load();

        int scoreboardRefreshInterval = FancyCorePlugin.get().getConfig().getScoreboardRefreshInterval();
        FancyCore.get().getThreadPool().scheduleWithFixedDelay(this::update, 0L, scoreboardRefreshInterval, TimeUnit.MILLISECONDS);
    }

    private void load() {
        List<ScoreboardPage> pages = storage.loadAllPages();
        if (pages == null || pages.isEmpty()) {
            FancyCorePlugin.get().getFancyLogger().error("No scoreboard pages loaded from storage!");
        }

        for (ScoreboardPage page : pages) {
            if (page != null && page.getName() != null) {
                cache.put(page.getName(), page);
            }
        }

        if (!cache.containsKey("default")) {
            FancyCorePlugin.get().getFancyLogger().warn(
                    "The default scoreboard page ('default') does not exist. Check your storage or create it manually."
            );
        }
    }

    private void update() {
        for (Map.Entry<FancyPlayer, ScoreboardUI> entry : playerScoreboards.entrySet()) {
            FancyPlayer fancyPlayer = entry.getKey();
            if (!fancyPlayer.isOnline()) {
                playerScoreboards.remove(fancyPlayer);
                continue;
            }

            ScoreboardUI scoreboardUI = entry.getValue();
            scoreboardUI.refreshUI();
        }
    }

    public void attachScoreboard(FancyPlayer fancyPlayer, ScoreboardPage page) {
        if (page == null) {
            FancyCorePlugin.get().getFancyLogger().error("Unable to attach scoreboard: page null for player " + fancyPlayer.getPlayer().getUsername());
            return;
        }

        if (!fancyPlayer.isOnline()) {
            if (playerScoreboards.containsKey(fancyPlayer)) {
                playerScoreboards.remove(fancyPlayer);
            }
            return;
        }

        Player player = fancyPlayer.getPlayer().getReference().getStore().getComponent(fancyPlayer.getPlayer().getReference(), Player.getComponentType());
        if (player == null) {
            return;
        }

        // Check if the player already has a scoreboard UI
        if (player.getHudManager().getCustomHud() instanceof ScoreboardUI sui) {
            sui.setCurrentPage(page);
            sui.refreshUI();

            if (!playerScoreboards.containsKey(fancyPlayer)) {
                playerScoreboards.put(fancyPlayer, sui);
            }
            return;
        }

        // Create a new scoreboard UI and attach it to the player
        ScoreboardUI scoreboardUI = new ScoreboardUI(fancyPlayer, page);
        player.getHudManager().setCustomHud(fancyPlayer.getPlayer(), scoreboardUI);

        playerScoreboards.put(fancyPlayer, scoreboardUI);
    }

    public void detachScoreboard(FancyPlayer fancyPlayer) {
        Player player = fancyPlayer.getPlayer().getReference().getStore().getComponent(fancyPlayer.getPlayer().getReference(), Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getHudManager().resetHud(fancyPlayer.getPlayer());

        playerScoreboards.remove(fancyPlayer);
    }

    @Override
    public ScoreboardPage getPage(String name) {
        return cache.get(name);
    }

    @Override
    public List<ScoreboardPage> getPages() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void createPage(ScoreboardPage page) {
        cache.put(page.getName(), page);
        storage.storePage(page);
    }

    @Override
    public void deletePage(String name) {
        cache.remove(name);
        storage.deletePage(name);
    }
}
