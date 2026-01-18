package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class FeedCMD extends AbstractPlayerCommand {

    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("target", "The player to feed", FancyCoreArgs.PLAYER);

    public FeedCMD() {
        super("feed", "Feeds a player to full hunger");
        requirePermission("fancycore.commands.feed");
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

            EntityStatMap entityStatMap = targetStore.getComponent(targetRef, EntityStatsModule.get().getEntityStatMapComponentType());
            if (entityStatMap == null) {
                if (ctx.isPlayer()) {
                    fp.sendMessage("Failed to get entity stat map for " + target.getData().getUsername() + ".");
                } else {
                    ctx.sendMessage(Message.raw("Failed to get entity stat map for " + target.getData().getUsername() + "."));
                }
                return;
            }

            // Use Stamina as the food/hunger stat (Hytale may not have a separate food stat)
            EntityStatValue staminaStat = entityStatMap.get(DefaultEntityStatTypes.getStamina());
            if (staminaStat == null) {
                if (ctx.isPlayer()) {
                    fp.sendMessage("Failed to get stamina stat for " + target.getData().getUsername() + ".");
                } else {
                    ctx.sendMessage(Message.raw("Failed to get stamina stat for " + target.getData().getUsername() + "."));
                }
                return;
            }

            float maxStamina = staminaStat.getMax();
            // Use reflection to access protected set() method
            try {
                java.lang.reflect.Method setMethod = staminaStat.getClass().getDeclaredMethod("set", float.class);
                setMethod.setAccessible(true);
                setMethod.invoke(staminaStat, maxStamina);
            } catch (Exception e) {
                if (ctx.isPlayer()) {
                    fp.sendMessage("Failed to set stamina: " + e.getMessage());
                } else {
                    ctx.sendMessage(Message.raw("Failed to set stamina: " + e.getMessage()));
                }
                return;
            }

            String message;
            if (target.getData().getUUID().equals(fp.getData().getUUID())) {
                message = "You have been fed to full hunger.";
            } else {
                message = target.getData().getUsername() + " has been fed to full hunger.";
            }

            if (ctx.isPlayer()) {
                fp.sendMessage(message);
                if (!target.getData().getUUID().equals(fp.getData().getUUID())) {
                    target.sendMessage("You have been fed to full hunger by " + fp.getData().getUsername() + ".");
                }
            } else {
                ctx.sendMessage(Message.raw(message));
                target.sendMessage("You have been fed to full hunger by console.");
            }
        });
    }
}
