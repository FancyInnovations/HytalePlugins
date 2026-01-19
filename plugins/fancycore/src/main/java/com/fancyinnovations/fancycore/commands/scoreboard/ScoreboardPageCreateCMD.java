package com.fancyinnovations.fancycore.commands.scoreboard;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardService;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.scoreboard.ScoreboardLineImpl;
import com.fancyinnovations.fancycore.scoreboard.ScoreboardPageImpl;
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

import java.util.ArrayList;

public class ScoreboardPageCreateCMD extends AbstractPlayerCommand {

    private final RequiredArg<String> nameArg = withRequiredArg("page name", "the new name", ArgTypes.STRING);

    public ScoreboardPageCreateCMD() {
        super("create", "Creates a new scoreboard page");
        requirePermission("fancycore.commands.scoreboard.page.create");
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

        String name = nameArg.get(ctx);

        ScoreboardPage page = new ScoreboardPageImpl(
                name,
                "Right",
                300,
                250,
                300,
                new ScoreboardPage.BackgroundColor((byte) 50, (byte) 0, (byte) 0, (byte) 0),
                new ArrayList<>()
        );
        page.addLine(new ScoreboardLineImpl(
                "&e&lServer name",
                "Center",
                32,
                20,
                5,
                0,
                0
        ));
        page.addLine(new ScoreboardLineImpl(
                "&7Welcome to the server!",
                "Center",
                22,
                0,
                15,
                0,
                0
        ));
        page.addLine(new ScoreboardLineImpl(
                "&6Player: &e%player_name%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        page.addLine(new ScoreboardLineImpl(
                "&6Group: &e%player_group_prefix%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        page.addLine(new ScoreboardLineImpl(
                "&6Money: &2$&a%player_balance%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        page.addLine(new ScoreboardLineImpl(
                "&6Playtime: &e%player_playtime%",
                null,
                null,
                null,
                null,
                null,
                null
        ));
        page.addLine(new ScoreboardLineImpl(
                "&7%online_players%/%max_players% online",
                "Center",
                22,
                15,
                5,
                10,
                0
        ));

        ScoreboardService.get().createPage(page);

        FancyCorePlugin.get().getScoreboardServiceImpl().attachScoreboard(fp, page);

        fp.sendMessage("Scoreboard page '" + name + "' created successfully.");
    }
}
