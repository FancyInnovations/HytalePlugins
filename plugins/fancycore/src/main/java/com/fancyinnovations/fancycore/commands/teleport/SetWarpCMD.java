package com.fancyinnovations.fancycore.commands.teleport;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.Location;
import com.fancyinnovations.fancycore.api.teleport.Warp;
import com.fancyinnovations.fancycore.api.teleport.WarpService;
import com.fancyinnovations.hytaleutils.NumberUtils;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeDoublePosition;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class SetWarpCMD extends AbstractWorldCommand {

    protected final RequiredArg<String> nameArg = this.withRequiredArg("warp", "name of the new warp", ArgTypes.STRING);
    private final OptionalArg<RelativeDoublePosition> positionArg = this.withOptionalArg("position", "position to set", ArgTypes.RELATIVE_POSITION);
    private final DefaultArg<Vector3f> rotationArg = this.withDefaultArg("rotation", "rotation to set", ArgTypes.ROTATION, Vector3f.FORWARD, "forward looking direction");

    public SetWarpCMD() {
        super("setwarp", "Creates a warp point at your current location with the specified name");
        addAliases("createwarp");
        requirePermission("fancycore.commands.setwarp");
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }
        Ref<EntityStore> playerRef = ctx.senderAsPlayerRef();

        String warpName = nameArg.get(ctx);
        if (warpName == null || warpName.trim().isEmpty()) {
            ctx.sendMessage(Message.raw("Warp name cannot be empty."));
            return;
        }

        // Check if warp already exists
        if (WarpService.get().getWarp(warpName) != null) {
            ctx.sendMessage(Message.raw("A warp with the name \"" + warpName + "\" already exists."));
            return;
        }

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

        Warp warp = new Warp(
                warpName,
                new Location(
                        world.getName(),
                        position.getX(),
                        position.getY(),
                        position.getZ(),
                        rotation.getYaw(),
                        rotation.getPitch()
                )
        );
        WarpService.get().setWarp(warp);

        fp.sendMessage("Set warp " + warpName + " at " + NumberUtils.formatNumber(warp.location().x()) + ", " + NumberUtils.formatNumber(warp.location().y()) + ", " + NumberUtils.formatNumber(warp.location().z()) + " in world '" + warp.location().worldName() + "'.");
    }
}
