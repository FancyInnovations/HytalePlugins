package com.fancyinnovations.fancycore.commands.permissions;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GroupArg {

    public static final String NAME = "group";
    public static final String DESCRIPTION = "The name of the player group";

    public static final SingleArgumentType<Group> TYPE = new SingleArgumentType<Group>("Group", "The name of the group", new String[]{"admin", "moderator", "member"}) {

        public @Nullable Group parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            Group group = PermissionService.get().getGroup(input);
            if (group == null) {
                parseResult.fail(Message.raw("Group '" + input + "' not found."));
                return null;
            }

            return group;
        }
    };

}
