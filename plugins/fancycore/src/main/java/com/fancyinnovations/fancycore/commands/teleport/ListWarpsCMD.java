package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.Warp;
import com.fancyinnovations.fancycore.api.teleport.WarpService;
import com.fancyinnovations.fancycore.uis.teleportation.WarpsPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListWarpsCMD extends AbstractPlayerCommand {

    public ListWarpsCMD() {
        super("listwarps", "Lists all available warp points on the server");
        addAliases("warps");
        requirePermission("fancycore.commands.listwarps");
    }
    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        List<Warp> warps = WarpService.get().getAllWarps();
        if (warps == null || warps.isEmpty()) {
            ctx.sendMessage(Message.raw("No warps have been created yet."));
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            ctx.sendMessage(Message.raw("Player component not found."));
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new WarpsPage(playerRef));

//        ctx.sendMessage(Message.raw("Available Warps:"));
//        for (Warp warp : warps) {
//            if (!PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.warps." + warp.name())) {
//                continue;
//            }
//            ctx.sendMessage(Message.raw("- " + warp.name()));
//        }
    }
}
