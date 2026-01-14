package com.fancyinnovations.fancycore.commands.permissions;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class GroupPermissionsSetCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg(GroupArg.NAME, GroupArg.DESCRIPTION, GroupArg.TYPE);
    protected final RequiredArg<String> permissionArg = this.withRequiredArg("permission", "the permission string to set", ArgTypes.STRING);
    protected final OptionalArg<Boolean> enabledArg = this.withOptionalArg("enabled", "whether the permission should be explicitly enabled or disabled", ArgTypes.BOOLEAN);

    protected GroupPermissionsSetCMD() {
        super("set", "Sets a permission for a group");
        requirePermission("fancycore.commands.groups.permissions.set");
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

        // TODO: Permission check
//        if (!fp.checkPermission("fancycore.commands.chatroom.delete")) {
//            fp.sendMessage(Message.raw("You do not have permission to delete a chat room."));
//            return;
//        }

        Group group = groupArg.get(ctx);
        String permission = permissionArg.get(ctx);
        boolean enabled = enabledArg.provided(ctx) ? enabledArg.get(ctx) : true;

        group.setPermission(permission, enabled);

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);

        fp.sendMessage("Set permission " + permission + " to " + enabled + " for group " + group.getName() + ".");
    }
}
