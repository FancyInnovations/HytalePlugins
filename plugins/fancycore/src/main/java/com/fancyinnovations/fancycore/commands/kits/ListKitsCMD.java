package com.fancyinnovations.fancycore.commands.kits;

import com.fancyinnovations.fancycore.kits.storage.KitStorage;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ListKitsCMD extends CommandBase {

    public ListKitsCMD() {
        super("listkits", "Lists all available kits");
        addAliases("kits");
        requirePermission("fancycore.commands.listkits");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        KitStorage kitStorage = com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getKitStorage();
        
        List<String> kitNames = kitStorage.getAllKitNames();

        if (kitNames == null || kitNames.isEmpty()) {
            ctx.sendMessage(Message.raw("No kits have been created yet."));
            return;
        }

        // Sort kit names
        String kitList = kitNames.stream()
                .sorted()
                .collect(Collectors.joining(", "));

        // Send message
        ctx.sendMessage(Message.raw("Available kits (" + kitNames.size() + "): " + kitList));
    }
}
