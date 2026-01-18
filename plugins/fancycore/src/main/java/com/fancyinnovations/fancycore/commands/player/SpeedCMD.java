package com.fancyinnovations.fancycore.commands.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class SpeedCMD extends AbstractPlayerCommand {

    protected final RequiredArg<Float> speedArg = this.withRequiredArg("speed", "Speed value (0.1 to 10.0)", ArgTypes.FLOAT);
    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("target", "The player to set speed for", FancyCoreArgs.PLAYER);

    public SpeedCMD() {
        super("speed", "Sets the walk and fly speed of a player");
        requirePermission("fancycore.commands.speed");
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        float speed = speedArg.get(ctx);
        if (speed < 0.1f || speed > 10.0f) {
            if (ctx.isPlayer()) {
                fp.sendMessage("Speed must be between 0.1 and 10.0.");
            } else {
                ctx.sendMessage(Message.raw("Speed must be between 0.1 and 10.0."));
            }
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

            MovementManager movementManager = targetStore.getComponent(targetRef, MovementManager.getComponentType());
            if (movementManager == null) {
                if (ctx.isPlayer()) {
                    fp.sendMessage("Failed to get movement manager for " + target.getData().getUsername() + ".");
                } else {
                    ctx.sendMessage(Message.raw("Failed to get movement manager for " + target.getData().getUsername() + "."));
                }
                return;
            }

            // Set base speed and fly speeds
            movementManager.getSettings().baseSpeed = speed;
            movementManager.getSettings().horizontalFlySpeed = speed;
            movementManager.getSettings().verticalFlySpeed = speed;
            movementManager.update(targetPlayerRef.getPacketHandler());

            String message;
            if (target.getData().getUUID().equals(fp.getData().getUUID())) {
                message = "Your speed has been set to " + String.format("%.1f", speed) + ".";
            } else {
                message = "Speed has been set to " + String.format("%.1f", speed) + " for " + target.getData().getUsername() + ".";
            }

            if (ctx.isPlayer()) {
                fp.sendMessage(message);
                if (!target.getData().getUUID().equals(fp.getData().getUUID())) {
                    target.sendMessage("Your speed has been set to " + String.format("%.1f", speed) + " by " + fp.getData().getUsername() + ".");
                }
            } else {
                ctx.sendMessage(Message.raw(message));
                target.sendMessage("Your speed has been set to " + String.format("%.1f", speed) + " by console.");
            }
        });
    }
}
