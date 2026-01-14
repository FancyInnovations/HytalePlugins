package com.fancyinnovations.fancycore.commands.permissions.groups;

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

public class GroupSetPrefixCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg(GroupArg.NAME, GroupArg.DESCRIPTION, GroupArg.TYPE);
    protected final RequiredArg<String> prefixArg = this.withRequiredArg("prefix", "the new prefix for the group", ArgTypes.STRING);

    protected GroupSetPrefixCMD() {
        super("setprefix", "Sets a group's prefix");
        requirePermission("fancycore.commands.groups.setprefix");
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
        String prefix = prefixArg.get(ctx);
        if (prefix.startsWith("\"")) {
            prefix = prefix.substring(1);
        }
        if (prefix.endsWith("\"")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }

        group.setPrefix(prefix);

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);

        fp.sendMessage("Set prefix of group " + group.getName() + " to " + prefix + ".");
    }
}
