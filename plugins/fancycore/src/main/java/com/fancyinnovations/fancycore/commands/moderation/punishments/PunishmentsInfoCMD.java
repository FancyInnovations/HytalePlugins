package com.fancyinnovations.fancycore.commands.moderation.punishments;

import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class PunishmentsInfoCMD extends CommandBase {

    protected final RequiredArg<Punishment> punishmentArg = this.withRequiredArg("punishment", "ID of the punishment", FancyCoreArgs.PUNISHMENT);

    public PunishmentsInfoCMD() {
        super("info", "Get detailed information about a specific punishment");
        requirePermission("fancycore.commands.punishments.info");
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

        Punishment punishment = punishmentArg.get(ctx);

        fp.sendMessage("Punishment Information:");
        fp.sendMessage("- ID: " + punishment.id());
        fp.sendMessage("- Type: " + punishment.type().name());
        fp.sendMessage("- Reason: " + punishment.reason());
        fp.sendMessage("- Issued By: " + FancyPlayerService.get().getByUUID(punishment.issuedBy()).getData().getUsername());
        fp.sendMessage("- Issued At: " + TimeUtils.formatDate(punishment.issuedAt()));
        long duration = punishment.expiresAt() - punishment.issuedAt();
        String formattedDuration = duration > 0 ? TimeUtils.formatTime(duration) : "Permanent";
        fp.sendMessage("- Duration: " + formattedDuration);
        fp.sendMessage("- Active: " + (punishment.isActive() ? "Yes" : "No") );
    }
}
