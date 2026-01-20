package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class TeleportPosCMD extends CommandBase {

    protected final RequiredArg<Double> xArg = this.withRequiredArg("", "X coordinate", ArgTypes.DOUBLE);
    protected final RequiredArg<Double> yArg = this.withRequiredArg("", "Y coordinate", ArgTypes.DOUBLE);
    protected final RequiredArg<Double> zArg = this.withRequiredArg("", "Z coordinate", ArgTypes.DOUBLE);
    protected final OptionalArg<World> worldArg = this.withOptionalArg("", "World name", ArgTypes.WORLD);

    public TeleportPosCMD() {
        super("teleportposition", "Teleports you to the specified coordinates");
        addAliases("tppos", "teleportpos");
        requirePermission("fancycore.commands.teleportposition");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        PlayerRef senderPlayerRef = fp.getPlayer();
        if (senderPlayerRef == null) {
            ctx.sendMessage(Message.raw("You are not online."));
            return;
        }

        Ref<EntityStore> senderRef = senderPlayerRef.getReference();
        if (senderRef == null || !senderRef.isValid()) {
            ctx.sendMessage(Message.raw("You are not in a world."));
            return;
        }

        // Get coordinates
        double x = xArg.get(ctx);
        double y = yArg.get(ctx);
        double z = zArg.get(ctx);

        // Execute teleportation on the correct world thread
        // We always read components and modify the store from within the world thread
        Store<EntityStore> senderStore = senderRef.getStore();
        if (senderStore == null) {
            ctx.sendMessage(Message.raw("Failed to get your entity store."));
            return;
        }
        
        World currentWorld = ((EntityStore) senderStore.getExternalData()).getWorld();
        if (currentWorld == null) {
            ctx.sendMessage(Message.raw("Failed to get your world."));
            return;
        }
        
        World targetWorld = worldArg.provided(ctx) ? worldArg.get(ctx) : currentWorld;

        // Save previous location for /back command (on current world thread)
        currentWorld.execute(() -> {
            TeleportLocationHelper.savePreviousLocation(fp, senderRef, senderStore, currentWorld);
        });

        targetWorld.execute(() -> {

            // Get current rotation to preserve it
            TransformComponent transformComponent = (TransformComponent) senderStore.getComponent(senderRef, TransformComponent.getComponentType());
            if (transformComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your transform."));
                return;
            }

            HeadRotation headRotationComponent = (HeadRotation) senderStore.getComponent(senderRef, HeadRotation.getComponentType());
            if (headRotationComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your head rotation."));
                return;
            }


            // Create teleport component
            Teleport teleport = new Teleport(targetWorld, new Vector3d(x, y, z), headRotationComponent.getRotation().clone());

            // Add teleport component to sender
            senderStore.addComponent(senderRef, Teleport.getComponentType(), teleport);

            // Send success message
            if (worldArg.provided(ctx)) {
                ctx.sendMessage(Message.raw("Teleported to (" + x + ", " + y + ", " + z + ") in world " + targetWorld.getName() + "."));
            } else {
                ctx.sendMessage(Message.raw("Teleported to (" + x + ", " + y + ", " + z + ")."));
            }
        });
    }
}
