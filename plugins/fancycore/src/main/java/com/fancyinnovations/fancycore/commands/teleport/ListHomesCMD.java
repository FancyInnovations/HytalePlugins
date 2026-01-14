package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ListHomesCMD extends CommandBase {

    public ListHomesCMD() {
        super("listhomes", "Lists all your home points");
        // TODO: Permission check
        // requirePermission("fancycore.commands.listhomes");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        // Get homes map
        Map<String, Object> customData = fp.getData().getCustomData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> homes = (Map<String, Map<String, Object>>) customData.get("homes");

        if (homes == null || homes.isEmpty()) {
            ctx.sendMessage(Message.raw("You do not have any homes set. Use /sethome <name> to set a home."));
            return;
        }

        // Get home names and sort them
        Set<String> homeNames = homes.keySet();
        String homeList = homeNames.stream()
                .sorted()
                .collect(Collectors.joining(", "));

        // Send message
        ctx.sendMessage(Message.raw("Your homes (" + homeNames.size() + "): " + homeList));
    }
}
