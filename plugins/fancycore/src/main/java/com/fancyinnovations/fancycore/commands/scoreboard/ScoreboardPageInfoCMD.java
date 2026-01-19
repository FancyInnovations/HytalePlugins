package com.fancyinnovations.fancycore.commands.scoreboard;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardLine;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class ScoreboardPageInfoCMD extends AbstractPlayerCommand {

    private final RequiredArg<ScoreboardPage> pageArg = withRequiredArg("page", "the scoreboard page", FancyCoreArgs.SCOREBOARD_PAGE);

    public ScoreboardPageInfoCMD() {
        super("info", "Get information about a scoreboard page");
        requirePermission("fancycore.commands.scoreboard.page.info");
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

        fp.sendMessage("Scoreboard Page Info:");
        fp.sendMessage("- Name: " + page.getName());
        fp.sendMessage("- Alignment: " + page.getAlignment());
        fp.sendMessage("- Offset: " + page.getOffset());
        fp.sendMessage("- Width: " + page.getWidth());
        fp.sendMessage("- Height: " + page.getHeight());
        ScoreboardPage.BackgroundColor bgColor = page.getBackgroundColor();
        fp.sendMessage("- Background Color - Alpha: " + bgColor.alpha() + ", Red: " + bgColor.red() + ", Green: " + bgColor.green() + ", Blue: " + bgColor.blue());
        fp.sendMessage("- Lines:");
        int i = 1;
        for (ScoreboardLine line : page.getLines()) {
            fp.sendMessage("  - " + i + ": " + line.getContent());
            i++;
        }
    }
}
