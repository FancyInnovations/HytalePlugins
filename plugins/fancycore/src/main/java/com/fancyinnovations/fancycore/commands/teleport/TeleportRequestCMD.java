package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.TeleportRequestService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

public class TeleportRequestCMD extends CommandBase {

    protected final RequiredArg<PlayerRef> targetArg = this.withRequiredArg("", "The player to request teleportation to", TeleportArg.TYPE);

    public TeleportRequestCMD() {
        super("tprequest", "Sends a teleport request to another player to teleport to their location");
        addAliases("tpr", "teleportrequest");
        requirePermission("fancycore.commands.tprequest");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer sender = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (sender == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        PlayerRef targetPlayerRef = targetArg.get(ctx);
        
        // Check if target player is online by checking their reference
        if (targetPlayerRef.getReference() == null || !targetPlayerRef.getReference().isValid()) {
            ctx.sendMessage(Message.raw("Target player is not online."));
            return;
        }

        FancyPlayer target = FancyPlayerService.get().getByUUID(targetPlayerRef.getUuid());
        if (target == null) {
            ctx.sendMessage(Message.raw("Target player not found."));
            return;
        }

        // Ensure the FancyPlayer has the PlayerRef set so sendMessage works
        if (target.getPlayer() == null) {
            target.setPlayer(targetPlayerRef);
        }

        if (sender.getData().getUUID().equals(target.getData().getUUID())) {
            ctx.sendMessage(Message.raw("You cannot send a teleport request to yourself."));
            return;
        }

        TeleportRequestService requestService = TeleportRequestService.get();
        if (requestService.sendRequest(sender, target)) {
            ctx.sendMessage(Message.raw("Teleport request sent to " + target.getData().getUsername() + "."));
            target.sendMessage(sender.getData().getUsername() + " has sent you a teleport request. Use /tpaccept to accept or /tpdeny to deny.");
        } else {
            ctx.sendMessage(Message.raw("You already have a pending teleport request to " + target.getData().getUsername() + "."));
        }
    }
}
