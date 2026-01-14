package com.fancyinnovations.fancycore.commands.teleport;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeleportArg {

    public static final String NAME = "target";
    public static final String DESCRIPTION = "The player to teleport";

    public static final SingleArgumentType<PlayerRef> TYPE = new SingleArgumentType<PlayerRef>("Player", "The player to teleport", new String[]{}) {

        public @Nullable PlayerRef parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            PlayerRef playerRef = ArgTypes.PLAYER_REF.parse(input, parseResult);
            if (playerRef == null) {
                parseResult.fail(Message.raw("Player \"" + input + "\" not found."));
                return null;
            }

            return playerRef;
        }
    };

}
