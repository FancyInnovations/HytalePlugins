package com.fancyinnovations.fancycore.commands.permissions;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GroupMembersListCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg(GroupArg.NAME, GroupArg.DESCRIPTION, GroupArg.TYPE);

    protected GroupMembersListCMD() {
        super("list", "Lists members of a player group");
        requirePermission("fancycore.commands.groups.members.list");
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

        fp.sendMessage("Members of group " + group.getName() + ":");
        if (group.getMembers().isEmpty()) {
            fp.sendMessage("  No members found.");
            return;
        }

        for (UUID memberUUID : group.getMembers()) {
            FancyPlayer memberFP = FancyPlayerService.get().getByUUID(memberUUID);
            if (memberFP != null) {
                fp.sendMessage("  - " + memberFP.getData().getUsername() + " (UUID: " + memberUUID + ")");
            } else {
                fp.sendMessage("  - Unknown Player (UUID: " + memberUUID + ")");
            }
        }
    }
}
