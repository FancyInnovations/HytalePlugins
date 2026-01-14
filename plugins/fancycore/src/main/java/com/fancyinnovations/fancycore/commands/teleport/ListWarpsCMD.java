package com.fancyinnovations.fancycore.commands.teleport;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ListWarpsCMD extends CommandBase {

    public ListWarpsCMD() {
        super("listwarps", "Lists all available warp points on the server");
        addAliases("warps");
        // TODO: Permission check
        // requirePermission("fancycore.commands.listwarps");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        com.fancyinnovations.fancycore.teleport.storage.WarpStorage warpStorage = 
                com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getWarpStorage();
        
        Map<String, Map<String, Object>> warps = warpStorage.getAllWarps();

        if (warps == null || warps.isEmpty()) {
            ctx.sendMessage(Message.raw("No warps have been created yet."));
            return;
        }

        // Get warp names and sort them
        Set<String> warpNames = warps.keySet();
        String warpList = warpNames.stream()
                .sorted()
                .collect(Collectors.joining(", "));

        // Send message
        ctx.sendMessage(Message.raw("Available warps (" + warpNames.size() + "): " + warpList));
    }
}
