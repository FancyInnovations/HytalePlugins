package com.fancyinnovations.fancycore.commands.permissions.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class PermissionsSetCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg(FancyPlayerArg.NAME, FancyPlayerArg.DESCRIPTION, FancyPlayerArg.TYPE);
    protected final RequiredArg<String> permissionArg = this.withRequiredArg("permission", "the permission string to set", ArgTypes.STRING);
    protected final OptionalArg<Boolean> enabledArg = this.withOptionalArg("enabled", "whether the permission should be explicitly enabled or disabled", ArgTypes.BOOLEAN);

    protected PermissionsSetCMD() {
        super("set", "Sets a permission for a player");
        requirePermission("fancycore.commands.permissions.set");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());

        FancyPlayer target = targetArg.get(ctx);
        String permission = permissionArg.get(ctx);
        boolean enabled = enabledArg.provided(ctx) ? enabledArg.get(ctx) : true;

        target.getData().setPermission(permission, enabled);

        if (ctx.isPlayer()) {
            fp.sendMessage("Set permission " + permission + " to " + enabled + " for player " + target.getData().getUsername() + ".");
        } else {
            ctx.sendMessage(Message.raw("Set permission " + permission + " to " + enabled + " for player " + target.getData().getUsername() + "."));
        }
    }
}
