package com.fancyinnovations.fancycore.commands.moderation.punishments;

import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.moderation.PunishmentService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PunishmentsListActiveCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg("target", "The player to list punishments for", FancyCoreArgs.PLAYER);

    public PunishmentsListActiveCMD() {
        super("listactive", "List all active punishments for a player");
        requirePermission("fancycore.commands.punishments.list");
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

        FancyPlayer target = targetArg.get(ctx);
        List<Punishment> punishments = PunishmentService.get().getPunishmentsForPlayer(target);
        if (punishments.isEmpty()) {
            fp.sendMessage("The player " + target.getData().getUsername() + " has no punishments.");
            return;
        }

        List<Punishment> sortedPunishments = punishments.stream()
                .sorted((p1, p2) -> Long.compare(p2.issuedAt(), p1.issuedAt()))
                .toList();

        fp.sendMessage("Active punishments for " + target.getData().getUsername() + ":");
        for (Punishment punishment : sortedPunishments) {
            if (!punishment.isActive()) {
                continue;
            }

            long duration = punishment.expiresAt() - punishment.issuedAt();
            String formattedDuration = duration > 0 ? TimeUtils.formatTime(duration) : "Permanent";
            String punishmentInfo = String.format("- [%s] [%s] [%s] Staff: %s Duration: %s Reason: \"%s\"",
                    punishment.id(),
                    TimeUtils.formatDate(punishment.issuedAt()),
                    punishment.type().name(),
                    FancyPlayerService.get().getByUUID(punishment.issuedBy()).getData().getUsername(),
                    formattedDuration,
                    punishment.reason()
            );
            fp.sendMessage(punishmentInfo);
        }
    }
}
