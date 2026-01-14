package com.fancyinnovations.fancycore.commands.permissions.groups;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class GroupPermissionsCMD extends AbstractCommandCollection {

    public GroupPermissionsCMD() {
        super("permissions", "Manages permissions for player groups");
        requirePermission("fancycore.commands.groups.permissions");

        addSubCommand(new GroupPermissionsListCMD());
        addSubCommand(new GroupPermissionsSetCMD());
        addSubCommand(new GroupPermissionsRemoveCMD());
        addSubCommand(new GroupPermissionsClearCMD());
    }

}
