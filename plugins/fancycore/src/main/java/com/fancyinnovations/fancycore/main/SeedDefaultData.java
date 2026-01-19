package com.fancyinnovations.fancycore.main;

import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardService;
import com.fancyinnovations.fancycore.permissions.GroupImpl;
import com.fancyinnovations.fancycore.permissions.PermissionImpl;
import com.fancyinnovations.fancycore.scoreboard.ScoreboardLineImpl;
import com.fancyinnovations.fancycore.scoreboard.ScoreboardPageImpl;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SeedDefaultData {

    public static final Group DEFAULT_GROUP = new GroupImpl(
            "member",
            0,
            new HashSet<>(),
            "&8[&7Member&8]&7",
            "",
            List.of(
                    new PermissionImpl("hytale.system.command.help", true),
                    new PermissionImpl("fancycore.commands.chatcolor", true),
                    new PermissionImpl("fancycore.commands.chatcolor", true),
                    new PermissionImpl("fancycore.commands.chatrooms", true),
                    new PermissionImpl("fancycore.commands.chatrooms.info", true),
                    new PermissionImpl("fancycore.commands.chatrooms.list", true),
                    new PermissionImpl("fancycore.commands.chatrooms.watching", true),
                    new PermissionImpl("fancycore.commands.chatrooms.watch", true),
                    new PermissionImpl("fancycore.commands.chatrooms.switch", true),
                    new PermissionImpl("fancycore.commands.sethome", true),
                    new PermissionImpl("fancycore.commands.deletehome", true),
                    new PermissionImpl("fancycore.commands.home", true),
                    new PermissionImpl("fancycore.commands.listhomes", true),
                    new PermissionImpl("fancycore.commands.spawn", true),
                    new PermissionImpl("fancycore.commands.warp", true),
                    new PermissionImpl("fancycore.commands.listwarps", true),
                    new PermissionImpl("fancycore.commands.message", true),
                    new PermissionImpl("fancycore.commands.ignore", true),
                    new PermissionImpl("fancycore.commands.unignore", true),
                    new PermissionImpl("fancycore.commands.reply", true),
                    new PermissionImpl("fancycore.commands.togglemessages", true),
                    new PermissionImpl("fancycore.kits.starter", true),
                    new PermissionImpl("fancycore.commands.kit", true),
                    new PermissionImpl("fancycore.commands.listkits", true),
                    new PermissionImpl("fancycore.commands.playerlist", true),
                    new PermissionImpl("fancycore.commands.teleportrequest", true),
                    new PermissionImpl("fancycore.commands.teleportaccept", true),
                    new PermissionImpl("fancycore.commands.teleportdeny", true)
            ),
            Map.of(
                    "max_homes", 2,
                    "max_backpacks", 1
            ),
            new HashSet<>()
    );

    public static void seed() {
        File configFile = new File("mods/FancyCore/data/");
        if (configFile.exists()) {
            return;
        }
        configFile.getParentFile().mkdirs();

        seedChatRooms();
        seedGroups();
        seedEconomy();
        seedKits();
        seedScoreboards();
    }

    private static void seedChatRooms() {
        ChatService.get().createChatRoom("global");
        ChatService.get().createChatRoom("staff");
    }

    private static void seedGroups() {
        PermissionService.get().addGroup(DEFAULT_GROUP);

        Group moderatorGroup = new GroupImpl(
                "moderator",
                100,
                new HashSet<>(List.of("member")),
                "&2[&a&lMOD&2]&a",
                "",
                List.of(
                        new PermissionImpl("*")
                ),
                new ConcurrentHashMap<>(),
                new HashSet<>()
        );
        PermissionService.get().addGroup(moderatorGroup);

        Group adminGroup = new GroupImpl(
                "admin",
                200,
                new HashSet<>(List.of("moderator")),
                "&6[&e&lADMIN&6]&6",
                "",
                List.of(
                        new PermissionImpl("*")
                ),
                new ConcurrentHashMap<>(),
                new HashSet<>()
        );
        PermissionService.get().addGroup(adminGroup);

        Group ownerGroup = new GroupImpl(
                "owner",
                300,
                new HashSet<>(),
                "&4[&c&lOWNER&4]&c",
                "",
                List.of(
                        new PermissionImpl("*")
                ),
                new ConcurrentHashMap<>(),
                new HashSet<>()
        );
        PermissionService.get().addGroup(ownerGroup);
    }

    private static void seedEconomy() {
        Currency moneyCurrency = new Currency(
                "Money",
                "$",
                2,
                "global"
        );
        CurrencyService.get().registerCurrency(moneyCurrency);

        Currency coinsCurrency = new Currency(
                "Coins",
                "",
                0,
                "global"
        );
        CurrencyService.get().registerCurrency(coinsCurrency);
    }

    private static void seedKits() {
        Kit starterKit = new Kit(
                "starter",
                "Starter Kit",
                "A kit for new players.",
                24 * 60 * 60 * 1000 // 24 hours
        );
        List<ItemStack> starterKitItems = List.of(
                new ItemStack("Bench_WorkBench", 1),
                new ItemStack("Food_Bread", 20),
                new ItemStack("Weapon_Sword_Wood", 1)
        );
        KitsService.get().createKit(starterKit, starterKitItems);
    }

    private static void seedScoreboards() {
        ScoreboardPage defaultPage = new ScoreboardPageImpl(
                "default",
                "Right",
                300,
                250,
                300,
                new ScoreboardPage.BackgroundColor((byte) 50, (byte) 0, (byte) 0, (byte) 0),
                new ArrayList<>()
        );
        defaultPage.addLine(new ScoreboardLineImpl(
                "&e&lServer name",
                "Center",
                32,
                20,
                5,
                0,
                0
        ));
        defaultPage.addLine(new ScoreboardLineImpl(
                "&7Welcome to the server!",
                "Center",
                22,
                0,
                15,
                0,
                0
        ));
        defaultPage.addLine(new ScoreboardLineImpl(
                "&6Player: &e%player_name%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        defaultPage.addLine(new ScoreboardLineImpl(
                "&6Group: &e%player_group_prefix%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        defaultPage.addLine(new ScoreboardLineImpl(
                "&6Money: &2$&a%player_balance%",
                null,
                null,
                null,
                5,
                10,
                null
        ));
        defaultPage.addLine(new ScoreboardLineImpl(
                "&6Playtime: &e%player_playtime%",
                null,
                null,
                null,
                null,
                null,
                null
        ));
        defaultPage.addLine(new ScoreboardLineImpl(
                "&7%online_players% / %max_players% online",
                "Center",
                22,
                15,
                5,
                10,
                0
        ));

        ScoreboardService.get().createPage(defaultPage);
    }

}
