package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class GodCMD extends AbstractPlayerCommand {

    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("target", "The player to toggle god mode for", FancyCoreArgs.PLAYER);

    public GodCMD() {
        super("god", "Toggles god mode for a player");
        requirePermission("fancycore.commands.god");
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        FancyPlayer target = targetArg.provided(ctx) ? targetArg.get(ctx) : fp;

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
            Player player = targetStore.getComponent(targetRef, Player.getComponentType());
            if (player == null) {
                if (ctx.isPlayer()) {
                    fp.sendMessage("Failed to get player component for " + target.getData().getUsername() + ".");
                } else {
                    ctx.sendMessage(Message.raw("Failed to get player component for " + target.getData().getUsername() + "."));
                }
                return;
            }

            ComponentAccessor<EntityStore> componentAccessor = targetStore;
            boolean hasInvulnerable = componentAccessor.getArchetype(targetRef).contains(Invulnerable.getComponentType());
            
            String message;
            if (hasInvulnerable) {
                // Remove invulnerability
                componentAccessor.tryRemoveComponent(targetRef, Invulnerable.getComponentType());
                if (target.getData().getUUID().equals(fp.getData().getUUID())) {
                    message = "God mode has been disabled.";
                } else {
                    message = "God mode has been disabled for " + target.getData().getUsername() + ".";
                }
            } else {
                // Add invulnerability
                componentAccessor.putComponent(targetRef, Invulnerable.getComponentType(), Invulnerable.INSTANCE);
                if (target.getData().getUUID().equals(fp.getData().getUUID())) {
                    message = "God mode has been enabled.";
                } else {
                    message = "God mode has been enabled for " + target.getData().getUsername() + ".";
                }
            }

            if (ctx.isPlayer()) {
                fp.sendMessage(message);
                if (!target.getData().getUUID().equals(fp.getData().getUUID())) {
                    target.sendMessage("God mode has been " + (hasInvulnerable ? "disabled" : "enabled") + " by " + fp.getData().getUsername() + ".");
                }
            } else {
                ctx.sendMessage(Message.raw(message));
                target.sendMessage("God mode has been " + (hasInvulnerable ? "disabled" : "enabled") + " by console.");
            }
        });
    }
}
