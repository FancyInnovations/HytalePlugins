package com.fancyinnovations.fancycore.commands.moderation.reports;

import com.fancyinnovations.fancycore.api.moderation.PlayerReport;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ReportsInfoCMD extends CommandBase {

    protected final RequiredArg<PlayerReport> reportArg = this.withRequiredArg("report", "ID of the report", FancyCoreArgs.REPORT);

    public ReportsInfoCMD() {
        super("info", "Get detailed information about a specific report");
        requirePermission("fancycore.commands.reports.info");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        PlayerReport report = reportArg.get(ctx);

        fp.sendMessage("Report Information:");
        fp.sendMessage("- ID: " + report.id());
        fp.sendMessage("- Reported Player: " + report.reportedPlayer().getData().getUsername());
        fp.sendMessage("- Reporting Player: " + report.reportingPlayer().getData().getUsername());
        fp.sendMessage("- Reason: " + report.reason());
        fp.sendMessage("- Reported At: " + TimeUtils.formatDate(report.reportedAt()));
        fp.sendMessage("- Resolved: " + (report.isResolved() ? "Yes" : "No") );
        fp.sendMessage("- Resolved At: " + (report.isResolved() ? TimeUtils.formatDate(report.resolvedAt()) : "N/A"));
    }
}
