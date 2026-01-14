package com.fancyinnovations.fancycore.commands.permissions.groups;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class GroupMembersCMD extends AbstractCommandCollection {

    public GroupMembersCMD() {
        super("members", "Manages members of player groups");
        requirePermission("fancycore.commands.groups.members");

        addSubCommand(new GroupMembersListCMD());
        addSubCommand(new GroupMembersClearCMD());
        addSubCommand(new GroupMembersAddCMD());
        addSubCommand(new GroupMembersRemoveCMD());
    }

}
