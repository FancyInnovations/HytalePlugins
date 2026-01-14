package com.fancyinnovations.fancycore.commands.permissions.groups;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.permissions.GroupImpl;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public class GroupCreateCMD extends CommandBase {

    protected final RequiredArg<String> groupNameArg = this.withRequiredArg("group name", GroupArg.DESCRIPTION, ArgTypes.STRING);

    protected GroupCreateCMD() {
        super("create", "Create a new group");
        requirePermission("fancycore.commands.groups.create");
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

        String name = groupNameArg.get(ctx);

        if (PermissionService.get().getGroup(name) != null) {
            fp.sendMessage("A group with the name " + name + " already exists.");
            return;
        }

        Group group = new GroupImpl(
                name,
                new HashSet<>(),
                "",
                "",
                new ArrayList<>(),
                new HashSet<>()
        );

        PermissionService.get().addGroup(group);

        fp.sendMessage("Group " + name + " has been created.");
    }
}
