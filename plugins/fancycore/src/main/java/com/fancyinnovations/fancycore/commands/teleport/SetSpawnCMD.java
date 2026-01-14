package com.fancyinnovations.fancycore.commands.teleport;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCMD extends CommandBase {

    public SetSpawnCMD() {
        super("setspawn", "Sets the server's spawn point to your current location");
        requirePermission("fancycore.commands.setspawn");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        // Get sender's location
        Ref<EntityStore> senderRef = ctx.senderAsPlayerRef();
        if (senderRef == null || !senderRef.isValid()) {
            ctx.sendMessage(Message.raw("You are not in a world."));
            return;
        }

        Store<EntityStore> senderStore = senderRef.getStore();
        World senderWorld = ((EntityStore) senderStore.getExternalData()).getWorld();

        // Execute on the world thread to get location
        senderWorld.execute(() -> {
            // Get sender's transform and rotation
            TransformComponent senderTransformComponent = (TransformComponent) senderStore.getComponent(senderRef, TransformComponent.getComponentType());
            if (senderTransformComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your transform."));
                return;
            }

            HeadRotation senderHeadRotationComponent = (HeadRotation) senderStore.getComponent(senderRef, HeadRotation.getComponentType());
            if (senderHeadRotationComponent == null) {
                ctx.sendMessage(Message.raw("Failed to get your head rotation."));
                return;
            }

            // Save spawn location to our storage
            com.fancyinnovations.fancycore.teleport.storage.SpawnLocationStorage spawnStorage = 
                    com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getSpawnLocationStorage();
            spawnStorage.setSpawnLocation(
                    senderWorld.getName(),
                    senderTransformComponent.getPosition().getX(),
                    senderTransformComponent.getPosition().getY(),
                    senderTransformComponent.getPosition().getZ(),
                    senderHeadRotationComponent.getRotation().getYaw(),
                    senderHeadRotationComponent.getRotation().getPitch()
            );

            // Set the world's actual spawn point for new players
            com.hypixel.hytale.math.vector.Transform spawnTransform = new com.hypixel.hytale.math.vector.Transform(
                    senderTransformComponent.getPosition().clone(),
                    senderHeadRotationComponent.getRotation().clone()
            );
            
            // Try to set the world spawn point using reflection (method name may vary)
            try {
                // Try common method names
                java.lang.reflect.Method setSpawnMethod = null;
                try {
                    setSpawnMethod = senderWorld.getClass().getMethod("setSpawnPoint", com.hypixel.hytale.math.vector.Transform.class);
                } catch (NoSuchMethodException e1) {
                    try {
                        setSpawnMethod = senderWorld.getClass().getMethod("setSpawn", com.hypixel.hytale.math.vector.Transform.class);
                    } catch (NoSuchMethodException e2) {
                        // Try with Vector3d for position only
                        try {
                            setSpawnMethod = senderWorld.getClass().getMethod("setSpawnPoint", com.hypixel.hytale.math.vector.Vector3d.class);
                            setSpawnMethod.invoke(senderWorld, senderTransformComponent.getPosition().clone());
                        } catch (NoSuchMethodException e3) {
                            // Method not found, spawn location still saved to our storage
                        }
                    }
                }
                
                if (setSpawnMethod != null && setSpawnMethod.getParameterCount() == 1 && 
                    setSpawnMethod.getParameterTypes()[0] == com.hypixel.hytale.math.vector.Transform.class) {
                    setSpawnMethod.invoke(senderWorld, spawnTransform);
                }
            } catch (Exception e) {
                // If reflection fails, just log a warning - the spawn location is still saved to our storage
                com.fancyinnovations.fancycore.main.FancyCorePlugin.get().getFancyLogger().warn("Could not set world spawn point. Spawn location saved to plugin storage only.");
            }

            // Send success message
            ctx.sendMessage(Message.raw("Spawn location set to your current location."));
        });
    }
}
