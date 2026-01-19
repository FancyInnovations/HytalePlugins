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

public class ScoreboardPageSetOpacityCMD extends AbstractPlayerCommand {

    private final RequiredArg<ScoreboardPage> pageArg = withRequiredArg("page", "the scoreboard page to modify", FancyCoreArgs.SCOREBOARD_PAGE);
    private final RequiredArg<Integer> opacityArg = withRequiredArg("opacity", "New opacity in percent", ArgTypes.INTEGER);

    public ScoreboardPageSetOpacityCMD() {
        super("setopacity", "Set the opacity of a scoreboard page");
        requirePermission("fancycore.commands.scoreboard.page.setopacity");
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
        int opacity = opacityArg.get(ctx);
        if (opacity <= 0 || opacity > 100) {
            ctx.sendMessage(Message.raw("Invalid opacity. Please specify a value between 1 and 100."));
            return;
        }

        page.setBackgroundColor(new ScoreboardPage.BackgroundColor(
                (byte) (opacity * 255 / 100),
                page.getBackgroundColor().red(),
                page.getBackgroundColor().green(),
                page.getBackgroundColor().blue()
        ));
        FancyCorePlugin.get().getScoreboardStorage().storePage(page);

        fp.sendMessage("Successfully set the opacity of scoreboard page" + page.getName() + " to " + opacity + "%.");
    }
}
