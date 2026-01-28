package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.Location;
import com.fancyinnovations.fancycore.api.teleport.SpawnService;
import com.fancyinnovations.hytaleutils.NumberUtils;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeDoublePosition;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class SetSpawnCMD extends AbstractWorldCommand {

    private final OptionalArg<RelativeDoublePosition> positionArg = this.withOptionalArg("position", "position to set", ArgTypes.RELATIVE_POSITION);
    private final DefaultArg<Vector3f> rotationArg = this.withDefaultArg("rotation", "rotation to set", ArgTypes.ROTATION, Vector3f.FORWARD, "forward looking direction");

    public SetSpawnCMD() {
        super("setspawn", "Sets the server's spawn point to your current location");
        addAliases("createspawn");
        requirePermission("fancycore.commands.setspawn");
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            fp.sendMessage("FancyPlayer not found.");
            return;
        }
        Ref<EntityStore> playerRef = ctx.senderAsPlayerRef();

        Vector3d position;
        if (this.positionArg.provided(ctx)) {
            RelativeDoublePosition relativePosition = this.positionArg.get(ctx);
            position = relativePosition.getRelativePosition(ctx, world, store);
        } else {
            TransformComponent transformComponent = store.getComponent(playerRef, TransformComponent.getComponentType());
            position = transformComponent.getPosition().clone();
        }

        Vector3f rotation;
        if (this.rotationArg.provided(ctx)) {
            rotation = this.rotationArg.get(ctx);
        } else  {
            HeadRotation headRotationComponent = store.getComponent(playerRef, HeadRotation.getComponentType());
            rotation = headRotationComponent.getRotation();
        }

        Location spawnLocation = new Location(
                world.getName(),
                position.getX(),
                position.getY(),
                position.getZ(),
                rotation.getYaw(),
                rotation.getPitch()
        );
        SpawnService.get().setSpawnLocation(spawnLocation);

        Transform transform = new Transform(position, rotation);
        WorldConfig worldConfig = world.getWorldConfig();
        worldConfig.setSpawnProvider(new GlobalSpawnProvider(transform));
        worldConfig.markChanged();

        fp.sendMessage("Spawn point set to " + NumberUtils.formatNumber(spawnLocation.x()) + ", " + NumberUtils.formatNumber(spawnLocation.y()) + ", " + NumberUtils.formatNumber(spawnLocation.z()) + " in world '" + spawnLocation.worldName() + "'.");
    }
}
