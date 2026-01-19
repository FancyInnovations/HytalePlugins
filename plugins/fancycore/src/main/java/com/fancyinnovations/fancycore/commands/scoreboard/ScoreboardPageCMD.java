package com.fancyinnovations.fancycore.commands.scoreboard;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class ScoreboardPageCMD extends AbstractCommandCollection {

    public ScoreboardPageCMD() {
        super("page", "Manage scoreboard pages");
        requirePermission("fancycore.commands.scoreboard.page");

        addSubCommand(new ScoreboardPageInfoCMD());
        addSubCommand(new ScoreboardPageCreateCMD());
        addSubCommand(new ScoreboardPageDeleteCMD());
        addSubCommand(new ScoreboardPageSetAlignmentCMD());
        addSubCommand(new ScoreboardPageSetWidthCMD());
        addSubCommand(new ScoreboardPageSetHeightCMD());
        addSubCommand(new ScoreboardPageSetOffsetCMD());
        addSubCommand(new ScoreboardPageSetBackgroundColorCMD());
        addSubCommand(new ScoreboardPageSetOpacityCMD());
    }
}
