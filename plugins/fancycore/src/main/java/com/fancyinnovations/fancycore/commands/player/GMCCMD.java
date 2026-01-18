package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class GMCCMD extends AbstractPlayerCommand {

    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("player", "Target player", FancyCoreArgs.PLAYER);

    public GMCCMD() {
        super("gmc", "Changes the gamemode of a player to creative mode");
        requirePermission("fancycore.commands.gamemode");
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        FancyPlayer target = targetArg.provided(ctx) ? targetArg.get(ctx) : fp;

        if (!target.getData().getUUID().equals(fp.getData().getUUID()) && !com.hypixel.hytale.server.core.permissions.PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.commands.gamemode.others")) {
            if (ctx.isPlayer()) {
                fp.sendMessage("You do not have permission to change gamemode for other players.");
            } else {
                ctx.sendMessage(Message.raw("You do not have permission to change gamemode for other players."));
            }
            return;
        }

        if (!target.isOnline()) {
            if (ctx.isPlayer()) {
                fp.sendMessage("The player " + target.getData().getUsername() + " is not online.");
            } else {
                ctx.sendMessage(Message.raw("The player " + target.getData().getUsername() + " is not online."));
            }
            return;
        }

        PlayerRef targetPlayerRef = target.getPlayer();
        if (targetPlayerRef == null) {
            if (ctx.isPlayer()) {
                fp.sendMessage("The player " + target.getData().getUsername() + " is not online.");
            } else {
                ctx.sendMessage(Message.raw("The player " + target.getData().getUsername() + " is not online."));
            }
            return;
        }

        Ref<EntityStore> targetRef = targetPlayerRef.getReference();
        if (targetRef == null || !targetRef.isValid()) {
            if (ctx.isPlayer()) {
                fp.sendMessage("The player " + target.getData().getUsername() + " is not in a world.");
            } else {
                ctx.sendMessage(Message.raw("The player " + target.getData().getUsername() + " is not in a world."));
            }
            return;
        }

        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = ((EntityStore) targetStore.getExternalData()).getWorld();

        targetWorld.execute(() -> {
            Player playerComponent = targetStore.getComponent(targetRef, Player.getComponentType());
            if (playerComponent == null) {
                if (ctx.isPlayer()) {
                    fp.sendMessage("Failed to get player component for " + target.getData().getUsername() + ".");
                } else {
                    ctx.sendMessage(Message.raw("Failed to get player component for " + target.getData().getUsername() + "."));
                }
                return;
            }

            if (playerComponent.getGameMode() == GameMode.Creative) {
                String message = "Player " + target.getData().getUsername() + " is already in creative mode.";
                if (ctx.isPlayer()) {
                    fp.sendMessage(message);
                } else {
                    ctx.sendMessage(Message.raw(message));
                }
                return;
            }

            Player.setGameMode(targetRef, GameMode.Creative, (ComponentAccessor<EntityStore>) targetStore);

            String message;
            if (target.getData().getUUID().equals(fp.getData().getUUID())) {
                message = "Your gamemode has been changed to creative.";
            } else {
                message = "Gamemode of " + target.getData().getUsername() + " has been changed to creative.";
            }

            if (ctx.isPlayer()) {
                fp.sendMessage(message);
                if (!target.getData().getUUID().equals(fp.getData().getUUID())) {
                    target.sendMessage("Your gamemode has been changed to creative by " + fp.getData().getUsername() + ".");
                }
            } else {
                ctx.sendMessage(Message.raw(message));
                target.sendMessage("Your gamemode has been changed to creative by console.");
            }
        });
    }
}
