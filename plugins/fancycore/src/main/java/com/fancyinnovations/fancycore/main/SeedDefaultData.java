package com.fancyinnovations.fancycore.main;

import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.permissions.GroupImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SeedDefaultData {

    public static void seed() {
        File pluginFolder = new File("mods/FancyCore");
        if (pluginFolder.exists()) {
            return;
        }
        pluginFolder.mkdirs();

        seedChatRooms();
        seedGroups();
        seedEconomy();
    }

    private static void seedChatRooms() {
        ChatService.get().createChatRoom("global");
    }

    private static void seedGroups() {
        Group memberGroup = new GroupImpl(
                "member",
                null,
                "[Member]",
                "",
                List.of(),
                new ArrayList<>()
        );
        PermissionService.get().addGroup(memberGroup);

        Group moderatorGroup = new GroupImpl(
                "moderator",
                null,
                "[Moderator]",
                "",
                List.of(),
                new ArrayList<>()
        );
        PermissionService.get().addGroup(moderatorGroup);

        Group ownerGroup = new GroupImpl(
                "owner",
                null,
                "[Owner]",
                "",
                List.of(),
                new ArrayList<>()
        );
        PermissionService.get().addGroup(ownerGroup);
    }

    private static void seedEconomy() {
        Currency primaryCurrency = new Currency(
                "Dollar",
                "$",
                2
        );
        CurrencyService.get().registerCurrency(primaryCurrency);
    }

}
