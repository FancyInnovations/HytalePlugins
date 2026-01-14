package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.TeleportRequestService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeleportDenyCMD extends CommandBase {

    protected final OptionalArg<PlayerRef> senderArg = this.withOptionalArg("player", "The player who sent the request", TeleportArg.TYPE);

    public TeleportDenyCMD() {
        super("tpdeny", "Denies a pending teleport request from another player");
        addAliases("tpd", "teleportdeny");
        // TODO: Permission check
        // requirePermission("fancycore.commands.tpdeny");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer target = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (target == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        TeleportRequestService requestService = TeleportRequestService.get();
        UUID senderUUID;

        if (senderArg.provided(ctx)) {
            // Specific player provided
            PlayerRef senderPlayerRef = senderArg.get(ctx);
            FancyPlayer sender = FancyPlayerService.get().getByUUID(senderPlayerRef.getUuid());
            if (sender == null) {
                ctx.sendMessage(Message.raw("Sender player not found."));
                return;
            }

            senderUUID = requestService.getRequest(target, sender);
            if (senderUUID == null) {
                ctx.sendMessage(Message.raw("You do not have a pending teleport request from " + sender.getData().getUsername() + "."));
                return;
            }
        } else {
            // No player specified, get first request
            senderUUID = requestService.getFirstRequest(target);
            if (senderUUID == null) {
                ctx.sendMessage(Message.raw("You do not have any pending teleport requests."));
                return;
            }
        }

        FancyPlayer sender = FancyPlayerService.get().getByUUID(senderUUID);
        if (sender == null || !sender.isOnline()) {
            ctx.sendMessage(Message.raw("The player who sent the request is no longer online."));
            requestService.removeAllRequests(target);
            return;
        }

        // Remove the request
        if (requestService.removeRequest(target, sender)) {
            ctx.sendMessage(Message.raw("Denied teleport request from " + sender.getData().getUsername() + "."));
            sender.sendMessage(target.getData().getUsername() + " denied your teleport request.");
        } else {
            ctx.sendMessage(Message.raw("Failed to deny teleport request."));
        }
    }
}
