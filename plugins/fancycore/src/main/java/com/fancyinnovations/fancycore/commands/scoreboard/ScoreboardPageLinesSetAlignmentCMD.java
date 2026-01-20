package com.fancyinnovations.fancycore.commands.scoreboard;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardLine;
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

public class ScoreboardPageLinesSetAlignmentCMD extends AbstractPlayerCommand {

    private final RequiredArg<ScoreboardPage> pageArg = withRequiredArg("page", "the scoreboard page", FancyCoreArgs.SCOREBOARD_PAGE);
    private final RequiredArg<Integer> lineArg = withRequiredArg("line", "the number of the line (starting from 1)", ArgTypes.INTEGER);
    private final RequiredArg<String> alignmentArg = withRequiredArg("alignment", "Center or Left", ArgTypes.STRING);

    public ScoreboardPageLinesSetAlignmentCMD() {
        super("setalignment", "Set the alignment of a scoreboard line");
        requirePermission("fancycore.commands.scoreboard.page.lines.setalignment");
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

        // TODO 'Right' alignment crashes the client
        String alignment = alignmentArg.get(ctx);
        if (alignment.equalsIgnoreCase("center")) {
            alignment = "Center";
        } else if (alignment.equalsIgnoreCase("left")) {
            alignment = "Left";
        } else {
            fp.sendMessage("Invalid alignment. It must be, 'Center' or 'Left'.");
            return;
        }

        ScoreboardPage page = pageArg.get(ctx);

        int lineNumber = lineArg.get(ctx);
        if (lineNumber < 1 || lineNumber > page.getLines().size()) {
            fp.sendMessage("Invalid line number. It must be between 1 and " + page.getLines().size() + ".");
            return;
        }
        ScoreboardLine line = page.getLines().get(lineNumber - 1);

        line.setAlignment(alignment);
        FancyCorePlugin.get().getScoreboardStorage().storePage(page);

        fp.sendMessage("Set alignment of line " + lineNumber + " on page '" + page.getName() + "' to: " + alignment);
    }
}
