package com.fancyinnovations.fancycore.commands.teleport;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class DeleteWarpCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("", "Warp name", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);

    public DeleteWarpCMD() {
        super("deletewarp", "Deletes the warp point with the specified name");
        // TODO: Permission check
        // requirePermission("fancycore.commands.deletewarp");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        String warpName = nameArg.get(ctx);
        if (warpName == null || warpName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Warp name cannot be empty."));
            return;
        }

        com.fancyinnovations.fancycore.teleport.storage.WarpStorage warpStorage = 
                com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getWarpStorage();

        if (!warpStorage.warpExists(warpName)) {
            ctx.sendMessage(Message.raw("Warp \"" + warpName + "\" does not exist."));
            return;
        }

        // Delete warp
        warpStorage.deleteWarp(warpName);

        // Send success message
        ctx.sendMessage(Message.raw("Warp \"" + warpName + "\" deleted."));
    }
}
