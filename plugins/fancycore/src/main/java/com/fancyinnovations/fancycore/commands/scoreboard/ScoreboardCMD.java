package com.fancyinnovations.fancycore.commands.scoreboard;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class ScoreboardCMD extends AbstractCommandCollection {

    public ScoreboardCMD() {
        super("scoreboard", "Manage scoreboards and their pages");
        addAliases("sb");
        requirePermission("fancycore.commands.scoreboard");

        addSubCommand(new ScoreboardListPagesCMD());
        addSubCommand(new ScoreboardSwitchPageCMD());
        addSubCommand(new ScoreboardPageCMD());
    }
}
