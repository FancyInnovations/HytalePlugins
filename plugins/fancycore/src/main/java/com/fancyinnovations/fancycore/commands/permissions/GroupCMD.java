package com.fancyinnovations.fancycore.commands.permissions;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class GroupCMD extends AbstractCommandCollection {

    public GroupCMD() {
        super("group", "Manages player groups");

        addSubCommand(new GroupInfoCMD());
        addSubCommand(new GroupListCMD());

        addSubCommand(new GroupCreateCMD());
        addSubCommand(new GroupDeleteCMD());
        addSubCommand(new GroupSetParentCMD());
        addSubCommand(new GroupSetPrefixCMD());
        addSubCommand(new GroupSetSuffixCMD());
        addSubCommand(new GroupPermissionsCMD());
        addSubCommand(new GroupMembersCMD());
    }

}
