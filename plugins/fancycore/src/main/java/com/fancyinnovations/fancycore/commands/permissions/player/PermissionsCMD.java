package com.fancyinnovations.fancycore.commands.permissions.player;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class PermissionsCMD extends AbstractCommandCollection {

    public PermissionsCMD() {
        super("permissions", "Manage player permissions");
        addAliases("permission", "perms");
        requirePermission("fancycore.commands.permissions");

        addSubCommand(new PermissionsCheckCMD());
        addSubCommand(new PermissionsListCMD());
        addSubCommand(new PermissionsSetCMD());
        addSubCommand(new PermissionsRemoveCMD());
    }
}
