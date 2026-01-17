package com.fancyinnovations.fancycore.commands.moderation.reports;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class ReportsCMD extends AbstractCommandCollection {

    public ReportsCMD() {
        super("reports", "Manage player reports");
        requirePermission("fancycore.commands.reports");

        addSubCommand(new ReportsListCMD());
        addSubCommand(new ReportsInfoCMD());
        addSubCommand(new ReportsResolveCMD());
    }
}
