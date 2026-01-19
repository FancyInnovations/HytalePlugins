package com.fancyinnovations.fancycore.commands.scoreboard;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class ScoreboardPageSetOffsetCMD extends AbstractPlayerCommand {

    private final RequiredArg<ScoreboardPage> pageArg = withRequiredArg("page", "the scoreboard page to modify", FancyCoreArgs.SCOREBOARD_PAGE);
    private final RequiredArg<Integer> offsetArg = withRequiredArg("offset", "New offset", ArgTypes.INTEGER);

    public ScoreboardPageSetOffsetCMD() {
        super("setoffset", "Set the offset of a scoreboard page");
        requirePermission("fancycore.commands.scoreboard.page.setoffset");
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

        ScoreboardPage page = pageArg.get(ctx);
        int offset = offsetArg.get(ctx);
        if (offset <= 0) {
            ctx.sendMessage(Message.raw("Invalid offset. Please specify a positive integer."));
            return;
        }

        page.setOffset(offset);
        FancyCorePlugin.get().getScoreboardStorage().storePage(page);

        fp.sendMessage("Scoreboard page '" + page.getName() + "' offset set to " + offset + ".");
    }
}
