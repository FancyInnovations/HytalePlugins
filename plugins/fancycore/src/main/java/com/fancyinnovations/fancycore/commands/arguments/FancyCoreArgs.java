package com.fancyinnovations.fancycore.commands.arguments;

import com.fancyinnovations.fancycore.api.chat.ChatRoom;
import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.Warp;
import com.fancyinnovations.fancycore.api.teleport.WarpService;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class FancyCoreArgs {

    public static final SingleArgumentType<Warp> WARP = new SingleArgumentType<>("Warp", "The name of the warp", new String[]{"spawn", "shop", "arena"}) {
        public @Nullable Warp parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            Warp warp = WarpService.get().getWarp(input);
            if (warp == null) {
                parseResult.fail(Message.raw("Warp '" + input + "' not found."));
                return null;
            }

            return warp;
        }
    };

    public static final SingleArgumentType<FancyPlayer> PLAYER = new SingleArgumentType<>("Player", "Username or UUID", new String[]{"OliverHD", "Simon", UUID.randomUUID().toString()}) {
        public @Nullable FancyPlayer parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            FancyPlayer fancyPlayer = FancyPlayerService.get().getByUsername(input);
            if (fancyPlayer == null) {
                try {
                    UUID uuid = UUID.fromString(input);
                    fancyPlayer = FancyPlayerService.get().getByUUID(uuid);
                } catch (IllegalArgumentException _) {
                }
            }

            if (fancyPlayer == null) {
                parseResult.fail(Message.raw("Player '" + input + "' not found."));
                return null;
            }

            return fancyPlayer;
        }
    };

    public static final SingleArgumentType<Group> GROUP = new SingleArgumentType<>("Group", "The name of the group", new String[]{"admin", "moderator", "member"}) {
        public @Nullable Group parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            Group group = PermissionService.get().getGroup(input);
            if (group == null) {
                parseResult.fail(Message.raw("Group '" + input + "' not found."));
                return null;
            }

            return group;
        }
    };

    public static final SingleArgumentType<Kit> KIT = new SingleArgumentType<>("Kit", "The name of the kit", new String[]{"pvp", "blocks"}) {
        public @Nullable Kit parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            Kit kit = KitsService.get().getKit(input);
            if (kit == null) {
                parseResult.fail(Message.raw("Kit '" + input + "' not found."));
                return null;
            }

            return kit;
        }
    };

    public static final SingleArgumentType<ChatRoom> CHATROOM = new SingleArgumentType<>("ChatRoom", "The name of the chatroom", new String[]{"global", "staff"}) {

        public @Nullable ChatRoom parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            ChatRoom chatRoom = ChatService.get().getChatRoom(input);
            if (chatRoom == null) {
                parseResult.fail(Message.raw("ChatRoom '" + input + "' not found."));
                return null;
            }

            return chatRoom;
        }
    };

    public static final SingleArgumentType<Currency> CURRENCY = new SingleArgumentType<>("Currency", "The name of the currency", new String[]{"dollar", "coin"}) {

        public @Nullable Currency parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            Currency currency = CurrencyService.get().getCurrency(input);
            if (currency == null) {
                parseResult.fail(Message.raw("Currency '" + input + "' not found."));
                return null;
            }

            return currency;
        }
    };

}
