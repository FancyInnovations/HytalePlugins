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

public class GroupSetSuffixCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg(GroupArg.NAME, GroupArg.DESCRIPTION, GroupArg.TYPE);
    protected final RequiredArg<String> suffixArg = this.withRequiredArg("suffix", "the new suffix for the group", ArgTypes.STRING);

    protected GroupSetSuffixCMD() {
        super("setsuffix", "Sets a group's suffix");
        requirePermission("fancycore.commands.groups.setsuffix");
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
        String suffix = suffixArg.get(ctx);
        if (suffix.startsWith("\"")) {
            suffix = suffix.substring(1);
        }
        if (suffix.endsWith("\"")) {
            suffix = suffix.substring(0, suffix.length() - 1);
        }

        group.setSuffix(suffix);

        FancyCorePlugin.get().getPermissionStorage().storeGroup(group);

        fp.sendMessage("Set suffix of group " + group.getName() + " to " + suffix + ".");
    }
}
