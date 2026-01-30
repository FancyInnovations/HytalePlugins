package com.fancyinnovations.fancycore.commands.fancycore;

import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.versionchecker.commands.UpdateCMD;
import com.fancyinnovations.versionchecker.commands.VersionCMD;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class FancyCoreCMD extends AbstractCommandCollection {

    public FancyCoreCMD() {
        super("fancycore", "Manage the FancyCore plugin");
        addAliases("fc");
        requirePermission("fancycore.commands.fancycore");

        addSubCommand(new VersionCMD("FancyCore", FancyCorePlugin.get().getVersionFetcher(), "fancycore.commands.fancycore.version"));
        addSubCommand(new UpdateCMD("FancyCore", FancyCorePlugin.get().getVersionChecker(), FancyCorePlugin.get().getFancyLogger(), "fancycore.commands.fancycore.update"));
        addSubCommand(new FancyCoreReloadConfigCMD());
    }
}
