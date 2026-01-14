package com.fancyinnovations.fancycore.commands.permissions.groups;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class GroupCMD extends AbstractCommandCollection {

    public GroupCMD() {
        super("groups", "Manages player groups");
        addAliases("group");
        requirePermission("fancycore.commands.groups");

        addSubCommand(new GroupInfoCMD());
        addSubCommand(new GroupListCMD());

        addSubCommand(new GroupCreateCMD());
        addSubCommand(new GroupDeleteCMD());
        addSubCommand(new GroupParentsAddCMD());
        addSubCommand(new GroupSetPrefixCMD());
        addSubCommand(new GroupSetSuffixCMD());
        addSubCommand(new GroupPermissionsCMD());
        addSubCommand(new GroupParentsCMD());
        addSubCommand(new GroupMembersCMD());
    }
}
