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

public class ScoreboardPageSetAlignmentCMD extends AbstractPlayerCommand {

    private final RequiredArg<ScoreboardPage> pageArg = withRequiredArg("page", "the scoreboard page to modify", FancyCoreArgs.SCOREBOARD_PAGE);
    private final RequiredArg<String> alignmentArg = withRequiredArg("alignment", "Right or Left", ArgTypes.STRING);

    public ScoreboardPageSetAlignmentCMD() {
        super("setalignment", "Set the alignment of a scoreboard page");
        requirePermission("fancycore.commands.scoreboard.page.setalignment");
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
        String alignment = alignmentArg.get(ctx);
        if (alignment.equalsIgnoreCase("right")) {
            alignment = "Right";
        } else if (alignment.equalsIgnoreCase("left")) {
            alignment = "Left";
        } else {
            ctx.sendMessage(Message.raw("Invalid alignment. Please specify 'Right' or 'Left'."));
            return;
        }

        page.setAlignment(alignment);
        FancyCorePlugin.get().getScoreboardStorage().storePage(page);

        fp.sendMessage("Scoreboard page '" + page.getName() + "' alignment set to " + alignment + ".");
    }
}
