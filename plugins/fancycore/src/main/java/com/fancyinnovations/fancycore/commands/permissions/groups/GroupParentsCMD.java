package com.fancyinnovations.fancycore.commands.permissions.groups;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class GroupParentsCMD extends AbstractCommandCollection {

    public GroupParentsCMD() {
        super("parents", "Manages parents of player groups");
        requirePermission("fancycore.commands.groups.parents");

        addSubCommand(new GroupParentsListCMD());
        addSubCommand(new GroupParentsClearCMD());
        addSubCommand(new GroupParentsAddCMD());
        addSubCommand(new GroupParentsRemoveCMD());
    }

}
