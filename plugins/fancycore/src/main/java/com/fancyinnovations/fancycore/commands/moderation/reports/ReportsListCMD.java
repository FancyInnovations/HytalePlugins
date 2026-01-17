package com.fancyinnovations.fancycore.commands.moderation.reports;

import com.fancyinnovations.fancycore.api.moderation.PlayerReport;
import com.fancyinnovations.fancycore.api.moderation.PunishmentService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class ReportsListCMD extends CommandBase {

    public ReportsListCMD() {
        super("list", "Lists all player reports");
        requirePermission("fancycore.commands.reports.list");
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

        fp.sendMessage("Unresolved Player Reports:");
        for (PlayerReport report : PunishmentService.get().getAllReports()) {
            if (report.isResolved()) {
                continue;
            }

            String reportInfo = String.format("- [%s] [%s] Reported: %s By: %s Reason: \"%s\"",
                    report.id(),
                    TimeUtils.formatDate(report.reportedAt()),
                    report.reportedPlayer().getData().getUsername(),
                    report.reportingPlayer().getData().getUsername(),
                    report.reason()
            );
            fp.sendMessage(reportInfo);
        }
    }
}
