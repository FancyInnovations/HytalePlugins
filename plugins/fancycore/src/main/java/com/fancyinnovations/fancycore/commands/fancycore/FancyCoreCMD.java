package com.fancyinnovations.fancycore.commands.fancycore;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class FancyCoreCMD extends AbstractCommandCollection {

    public FancyCoreCMD() {
        super("fancycore", "Manage the FancyCore plugin");
        addAliases("fc");
        requirePermission("fancycore.commands.fancycore");

        addSubCommand(new FancyCoreVersionCMD());
        addSubCommand(new FancyCoreUpdateCMD());
        addSubCommand(new FancyCoreReloadConfigCMD());
    }
}
