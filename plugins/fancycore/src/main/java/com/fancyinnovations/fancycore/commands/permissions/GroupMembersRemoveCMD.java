package com.fancyinnovations.fancycore.commands.permissions;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.player.FancyPlayerArg;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class GroupMembersRemoveCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg(GroupArg.NAME, GroupArg.DESCRIPTION, GroupArg.TYPE);
    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg(FancyPlayerArg.NAME, FancyPlayerArg.DESCRIPTION, FancyPlayerArg.TYPE);

    protected GroupMembersRemoveCMD() {
        super("remove", "Removes a member to a player group");
        requirePermission("fancycore.commands.groups.members.remove");
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
        FancyPlayer target = targetArg.get(ctx);

        if (!group.getMembers().contains(target.getData().getUUID())) {
            fp.sendMessage("Player " + target.getData().getUsername() + " is not a member of group " + group.getName() + ".");
            return;
        }

        group.getMembers().remove(target.getData().getUUID());

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);

        fp.sendMessage("Player " + target.getData().getUsername() + " has been removed from group " + group.getName() + ".");
    }
}
