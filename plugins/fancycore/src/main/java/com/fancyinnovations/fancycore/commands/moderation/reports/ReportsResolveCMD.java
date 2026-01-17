package com.fancyinnovations.fancycore.commands.moderation.reports;

import com.fancyinnovations.fancycore.api.moderation.PlayerReport;
import com.fancyinnovations.fancycore.api.moderation.PunishmentService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ReportsResolveCMD extends CommandBase {

    protected final RequiredArg<PlayerReport> reportArg = this.withRequiredArg("report", "ID of the report to resolve", FancyCoreArgs.REPORT);

    public ReportsResolveCMD() {
        super("resolve", "Resolve a specific player report");
        requirePermission("fancycore.commands.reports.resolve");
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
        if (report.isResolved()) {
            fp.sendMessage("This report has already been resolved.");
            return;
        }

        PunishmentService.get().resolveReport(report);

        fp.sendMessage("Successfully resolved report ID: " + report.id());
    }
}
