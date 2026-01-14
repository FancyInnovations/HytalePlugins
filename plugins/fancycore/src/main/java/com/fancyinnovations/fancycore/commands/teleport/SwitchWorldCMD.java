package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class SwitchWorldCMD extends AbstractPlayerCommand {

    protected final RequiredArg<World> worldNameArg = this.withRequiredArg("destination", "destination world", ArgTypes.WORLD);

    public SwitchWorldCMD() {
        super("switchworld", "Teleport to another world");
        requirePermission("fancycore.commands.switchworld");
    }

    @Override
    protected void execute(CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            fp.sendMessage("FancyPlayer not found.");
            return;
        }

        World destinationWorld = worldNameArg.get(ctx);

        Transform spawnPoint = destinationWorld.getWorldConfig().getSpawnProvider().getSpawnPoint(ref, store);
        if (spawnPoint == null) {
            fp.sendMessage("Could not find a valid spawn point in world " + destinationWorld.getName() + ".");
            return;
        }

        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());

        Vector3d previousPos = transformComponent.getPosition().clone();
        Vector3f previousRotation = headRotationComponent.getRotation().clone();

        TeleportHistory teleportHistoryComponent = store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
        teleportHistoryComponent.append(world, previousPos, previousRotation, "World " + destinationWorld.getName());

        store.addComponent(ref, Teleport.getComponentType(), new Teleport(destinationWorld, spawnPoint));
        Vector3d spawnPos = spawnPoint.getPosition();

        fp.sendMessage("Teleported to world " + destinationWorld.getName() + " at spawn point (" + spawnPos.getX() + ", " + spawnPos.getY() + ", " + spawnPos.getZ() + ").");
    }
}
