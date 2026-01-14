package com.fancyinnovations.fancycore.commands.permissions;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GroupMembersClearCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg(GroupArg.NAME, GroupArg.DESCRIPTION, GroupArg.TYPE);

    protected GroupMembersClearCMD() {
        super("clear", "Clears the members of a player group");
        requirePermission("fancycore.commands.groups.members.clear");
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

        group.setMembers(new ArrayList<>());

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);

        fp.sendMessage("Cleared all members of group " + group.getName() + ".");
    }
}
