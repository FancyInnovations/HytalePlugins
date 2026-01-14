package com.fancyinnovations.fancycore.commands.permissions.player;

import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class PermissionsListCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg(FancyPlayerArg.NAME, FancyPlayerArg.DESCRIPTION, FancyPlayerArg.TYPE);

    protected PermissionsListCMD() {
        super("list", "Lists permissions assigned to a player");
        requirePermission("fancycore.commands.permissions.list");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());

        FancyPlayer target = targetArg.get(ctx);

        if (ctx.isPlayer()) {
            fp.sendMessage("Permissions for player " + target.getData().getUsername() + ":");
            if (target.getData().getPermissions().isEmpty()) {
                fp.sendMessage("  No permissions assigned.");
                return;
            }

            for (Permission perm : target.getData().getPermissions()) {
                fp.sendMessage("  - " + perm.getPermission() + "(enabled: " + perm.isEnabled() + ")");
            }
        } else {
            ctx.sendMessage(Message.raw("Permissions for player " + target.getData().getUsername() + ":"));
            if (target.getData().getPermissions().isEmpty()) {
                ctx.sendMessage(Message.raw("  No permissions assigned."));
                return;
            }

            for (Permission perm : target.getData().getPermissions()) {
                ctx.sendMessage(Message.raw("  - " + perm.getPermission() + "(enabled: " + perm.isEnabled() + ")"));
            }
        }
    }
}
