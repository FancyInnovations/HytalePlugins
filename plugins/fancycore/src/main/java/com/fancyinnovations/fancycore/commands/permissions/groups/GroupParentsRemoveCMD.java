package com.fancyinnovations.fancycore.commands.permissions.groups;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class GroupParentsRemoveCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg(GroupArg.NAME, GroupArg.DESCRIPTION, GroupArg.TYPE);
    protected final RequiredArg<Group> parentArg = this.withRequiredArg("parent", GroupArg.DESCRIPTION, GroupArg.TYPE);

    protected GroupParentsRemoveCMD() {
        super("remove", "Removes a parent group to a group");
        requirePermission("fancycore.commands.groups.parents.remove");
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

        Group group = groupArg.get(ctx);
        Group parent = parentArg.get(ctx);

        group.removeParent(parent.getName());

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);

        fp.sendMessage("Removed parent group " + parent.getName() + " from group " + group.getName() + ".");
    }
}
