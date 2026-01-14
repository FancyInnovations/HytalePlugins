package com.fancyinnovations.fancycore.commands.kits;

import com.fancyinnovations.fancycore.kits.storage.KitStorage;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class DeleteKitCMD extends CommandBase {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("", "Kit name", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);

    public DeleteKitCMD() {
        super("deletekit", "Deletes the specified kit");
        requirePermission("fancycore.commands.deletekit");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        String kitName = nameArg.get(ctx);
        if (kitName == null || kitName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Kit name cannot be empty."));
            return;
        }

        KitStorage kitStorage = com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getKitStorage();

        if (!kitStorage.kitExists(kitName)) {
            ctx.sendMessage(Message.raw("Kit \"" + kitName + "\" does not exist."));
            return;
        }

        // Delete kit
        kitStorage.deleteKit(kitName);

        // Send success message
        ctx.sendMessage(Message.raw("Kit \"" + kitName + "\" deleted."));
    }
}
