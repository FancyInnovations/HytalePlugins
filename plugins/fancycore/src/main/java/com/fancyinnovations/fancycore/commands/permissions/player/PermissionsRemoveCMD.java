package com.fancyinnovations.fancycore.commands.permissions.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class PermissionsRemoveCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg(FancyPlayerArg.NAME, FancyPlayerArg.DESCRIPTION, FancyPlayerArg.TYPE);
    protected final RequiredArg<String> permissionArg = this.withRequiredArg("permission", "the permission string to remove", ArgTypes.STRING);

    protected PermissionsRemoveCMD() {
        super("remove", "Removes a permission from a player");
        requirePermission("fancycore.commands.permissions.remove");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());

        FancyPlayer target = targetArg.get(ctx);
        String permission = permissionArg.get(ctx);

        target.getData().removePermission(permission);

        if (ctx.isPlayer()) {
            fp.sendMessage("Removed permission " + permission + " from player " + target.getData().getUsername() + ".");
        } else {
            ctx.sendMessage(Message.raw("Removed permission " + permission + " from player " + target.getData().getUsername() + "."));
        }
    }
}
