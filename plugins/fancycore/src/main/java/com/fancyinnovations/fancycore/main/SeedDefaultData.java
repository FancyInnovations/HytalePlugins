package com.fancyinnovations.fancycore.main;

import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.permissions.GroupImpl;
import com.fancyinnovations.fancycore.permissions.PermissionImpl;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.io.File;
import java.util.HashSet;
import java.util.List;

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
                "A kit for new players."
        );
        List<ItemStack> starterKitItems = List.of(
                new ItemStack("Bench_WorkBench", 1),
                new ItemStack("Food_Bread", 20),
                new ItemStack("Weapon_Sword_Wood", 1)
        );
        KitsService.get().createKit(starterKit, starterKitItems);
    }

}
