package com.fancyinnovations.fancycore.commands.permissions.groups;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.Permission;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class GroupInfoCMD extends CommandBase {

    protected final RequiredArg<Group> groupArg = this.withRequiredArg("group", "name of the group", FancyCoreArgs.GROUP);

    protected GroupInfoCMD() {
        super("info", "Get information about a group");
        requirePermission("fancycore.commands.groups.info");
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

        fp.sendMessage("Group Info:");
        fp.sendMessage("- Name: " + group.getName());
        fp.sendMessage("- Weight: " + group.getWeight());
        fp.sendMessage("- Prefix: " + (group.getPrefix() != null ? group.getPrefix() : "None"));
        fp.sendMessage("- Suffix: " + (group.getSuffix() != null ? group.getSuffix() : "None"));
        fp.sendMessage("- Parents: ");
        if (group.getParents().isEmpty()) {
            fp.sendMessage("  No parent groups.");
        } else {
            for (String parent : group.getParents()) {
                fp.sendMessage("  - " + parent);
            }
        }

        fp.sendMessage("- Permissions: ");
        for (Permission permission : group.getPermissions()) {
            fp.sendMessage("  - Permission: " + permission.getPermission() + "  (Enabled: " + permission.isEnabled() + ")");
        }

        fp.sendMessage("- Metadata: ");
        for (Map.Entry<String, Object> entry : group.getMetadata().entrySet()) {
            fp.sendMessage("  - Metadata: " + entry.getKey() + " = " + entry.getValue().toString());
        }

        fp.sendMessage("- Members: ");
        for (UUID member : group.getMembers()) {
            FancyPlayer memberFP = FancyPlayerService.get().getByUUID(member);
            if (memberFP == null) {
                fp.sendMessage("  - Member: Unknown Player (UUID: " + member.toString() + ")");
                continue;
            }
            fp.sendMessage("  - Member: " + memberFP.getData().getUsername() + " (UUID: " + member.toString() + ")");
        }

    }
}
