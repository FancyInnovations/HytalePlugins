package com.fancyinnovations.fancycore.commands.permissions;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class GroupPermissionsRemoveCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg(GroupArg.NAME, GroupArg.DESCRIPTION, GroupArg.TYPE);
    protected final RequiredArg<String> permissionArg = this.withRequiredArg("permission", "the permission string to remove", ArgTypes.STRING);

    protected GroupPermissionsRemoveCMD() {
        super("remove", "Removes a permission from a group");
        requirePermission("fancycore.commands.groups.permissions.remove");
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

        group.getPermissions().removeIf(perm -> perm.getPermission().equalsIgnoreCase(permission));

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);

        fp.sendMessage("Removed permission " + permission + " from group " + group.getName() + ".");
    }
}
