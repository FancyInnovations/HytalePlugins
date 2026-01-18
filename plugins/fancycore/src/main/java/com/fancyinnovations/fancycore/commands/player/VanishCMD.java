package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VanishCMD extends AbstractAsyncCommand {

    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("target", "The player to toggle vanish for", FancyCoreArgs.PLAYER);

    public VanishCMD() {
        super("vanish", "Toggles vanish mode for a player");
        requirePermission("fancycore.commands.vanish");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(context.sender().getUuid());
        if (fp == null) {
            context.sendMessage(Message.raw("FancyPlayer not found."));
            return CompletableFuture.completedFuture(null);
        }

        FancyPlayer target = targetArg.provided(context) ? targetArg.get(context) : fp;

        if (!target.isOnline()) {
            if (context.isPlayer()) {
                fp.sendMessage("The player " + target.getData().getUsername() + " is not online.");
            } else {
                context.sendMessage(Message.raw("The player " + target.getData().getUsername() + " is not online."));
            }
            return CompletableFuture.completedFuture(null);
        }

        PlayerRef targetPlayerRef = target.getPlayer();
        if (targetPlayerRef == null) {
            if (context.isPlayer()) {
                fp.sendMessage("The player " + target.getData().getUsername() + " is not online.");
            } else {
                context.sendMessage(Message.raw("The player " + target.getData().getUsername() + " is not online."));
            }
            return CompletableFuture.completedFuture(null);
        }

        Ref<EntityStore> targetRef = targetPlayerRef.getReference();
        if (targetRef == null || !targetRef.isValid()) {
            if (context.isPlayer()) {
                fp.sendMessage("The player " + target.getData().getUsername() + " is not in a world.");
            } else {
                context.sendMessage(Message.raw("The player " + target.getData().getUsername() + " is not in a world."));
            }
            return CompletableFuture.completedFuture(null);
        }

        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = ((EntityStore) targetStore.getExternalData()).getWorld();

        return runAsync(context, () -> {
            Player playerComponent = targetStore.getComponent(targetRef, Player.getComponentType());
            if (playerComponent == null) {
                if (context.isPlayer()) {
                    fp.sendMessage("Failed to get player component for " + target.getData().getUsername() + ".");
                } else {
                    context.sendMessage(Message.raw("Failed to get player component for " + target.getData().getUsername() + "."));
                }
                return;
            }

            UUIDComponent uuidComponent = targetStore.getComponent(targetRef, UUIDComponent.getComponentType());
            if (uuidComponent == null) {
                if (context.isPlayer()) {
                    fp.sendMessage("Failed to get UUID component for " + target.getData().getUsername() + ".");
                } else {
                    context.sendMessage(Message.raw("Failed to get UUID component for " + target.getData().getUsername() + "."));
                }
                return;
            }

            UUID playerUuid = uuidComponent.getUuid();
            
            // Get current vanish state from stored data
            boolean isCurrentlyVanished = target.getData().isVanished();

            // Toggle vanish: hide from all players or show to all players
            boolean newVanishedState = !isCurrentlyVanished;
            for (World world : Universe.get().getWorlds().values()) {
                Collection<PlayerRef> players = world.getPlayerRefs();
                for (PlayerRef otherPlayerRef : players) {
                    if (!otherPlayerRef.getUuid().equals(playerUuid)) {
                        if (newVanishedState) {
                            otherPlayerRef.getHiddenPlayersManager().hidePlayer(playerUuid);
                        } else {
                            otherPlayerRef.getHiddenPlayersManager().showPlayer(playerUuid);
                        }
                    }
                }
            }

            // Save the new vanish state
            target.getData().setVanished(newVanishedState);
            com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getPlayerStorage().savePlayer(target.getData());

            String message;
            if (target.getData().getUUID().equals(fp.getData().getUUID())) {
                message = newVanishedState ? "Vanish mode has been enabled." : "Vanish mode has been disabled.";
            } else {
                message = newVanishedState 
                    ? "Vanish mode has been enabled for " + target.getData().getUsername() + "."
                    : "Vanish mode has been disabled for " + target.getData().getUsername() + ".";
            }

            if (context.isPlayer()) {
                fp.sendMessage(message);
                if (!target.getData().getUUID().equals(fp.getData().getUUID())) {
                    target.sendMessage("Vanish mode has been " + (newVanishedState ? "enabled" : "disabled") + " by " + fp.getData().getUsername() + ".");
                }
            } else {
                context.sendMessage(Message.raw(message));
                target.sendMessage("Vanish mode has been " + (newVanishedState ? "enabled" : "disabled") + " by console.");
            }
        }, targetWorld);
    }
}
