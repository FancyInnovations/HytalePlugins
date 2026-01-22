package com.fancyinnovations.fancycore.listeners;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.events.player.PlayerJoinedEvent;
import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardService;
import com.fancyinnovations.fancycore.api.teleport.Location;
import com.fancyinnovations.fancycore.api.teleport.SpawnService;
import com.fancyinnovations.fancycore.api.translations.MessageKey;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.main.SeedDefaultData;
import com.fancyinnovations.fancycore.player.FancyPlayerDataImpl;
import com.fancyinnovations.fancycore.player.FancyPlayerImpl;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerJoinListener {

    private final static FancyPlayerServiceImpl playerService = (FancyPlayerServiceImpl) FancyCorePlugin.get().getPlayerService();

    public static void onPlayerConnect(PlayerConnectEvent event) {
        boolean firstJoin = false;

        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(event.getPlayerRef().getUuid());
        if (fp == null) {
            FancyPlayerDataImpl newFancyPlayerData = new FancyPlayerDataImpl(event.getPlayerRef().getUuid(), event.getPlayerRef().getUsername());

            // Default group assignment
            Group defaultGroup = PermissionService.get().getGroup(FancyCorePlugin.get().getConfig().getDefaultGroupName());
            if (defaultGroup == null) {
                defaultGroup = SeedDefaultData.DEFAULT_GROUP;
                defaultGroup.addMember(event.getPlayerRef().getUuid());
                PermissionService.get().addGroup(defaultGroup);
            }
            newFancyPlayerData.addGroup(defaultGroup.getName());

            fp = new FancyPlayerImpl(
                    newFancyPlayerData,
                    event.getPlayerRef()
            );

            // Add to cache and save to database
            playerService.addPlayerToCache(fp);
            FancyCorePlugin.get().getPlayerStorage().savePlayer(fp.getData());

            firstJoin = true;
        }

        fp.setPlayer(event.getPlayerRef());

        fp.getCurrentChatRoom(); // Ensure default chat room is set

        // Update last login time
        fp.getData().setLastLoginTime(System.currentTimeMillis());

        Punishment punishment = fp.isBanned();
        if (punishment != null) {
            if (punishment.expiresAt() < 0) {
                String banMessage = FancyCore.get().getTranslationService()
                        .getMessage(MessageKey.BAN_PERMANENT)
                        .replace("reason", punishment.reason())
                        .getParsedMessage();
                event.getPlayerRef().getPacketHandler().disconnect(banMessage);
            } else {
                String duration = TimeUtils.formatTime(punishment.remainingDuration());
                String banMessage = FancyCore.get().getTranslationService()
                        .getMessage(MessageKey.BAN_TEMPORARY)
                        .replace("reason", punishment.reason())
                        .replace("duration", duration)
                        .getParsedMessage();
                event.getPlayerRef().getPacketHandler().disconnect(banMessage);
            }
            return;
        }

        if (firstJoin) {
            for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
                String firstJoinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getFirstJoinMessage());
                onlinePlayer.sendMessage(firstJoinMsg);
            }
            FancyCorePlugin.get().getPlayerStorage().savePlayer(fp.getData());

            String firstJoinKitName = FancyCorePlugin.get().getConfig().getFirstJoinKitName();
            if (firstJoinKitName != null && !firstJoinKitName.isEmpty()) {
                Kit kit = KitsService.get().getKit(firstJoinKitName);
                if (kit == null) {
                    FancyCorePlugin.get().getFancyLogger().warn("First join kit '" + firstJoinKitName + "' not found! Cannot give to player " + fp.getData().getUsername());
                } else {
                    // TODO Fix this workaround for giving kit after player is fully loaded
                    FancyPlayerImpl finalFp = fp;
                    FancyCorePlugin.get().getThreadPool().schedule(
                            () -> KitsService.get().giveKitToPlayer(kit, finalFp),
                            1, TimeUnit.SECONDS
                    );
                }
            }

            String joinMessage = PlaceholderService.get().parse(fp, FancyCorePlugin.get().getConfig().getFirstJoinMessage());
            fp.sendMessage(joinMessage);
        } else {
            String joinMsg = PlaceholderService.get().parse(fp, FancyCore.get().getConfig().getJoinMessage());
            for (FancyPlayer onlinePlayer : playerService.getOnlinePlayers()) {
                onlinePlayer.sendMessage(joinMsg);
            }
        }

        fp.setJoinedAt(System.currentTimeMillis());

        new PlayerJoinedEvent(fp, firstJoin).fire();
    }

    public static void onPlayerReady(PlayerReadyEvent event) {
        Ref<EntityStore> ref = event.getPlayerRef();
        Store<EntityStore> store = event.getPlayerRef().getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        UUIDComponent uuidComponent = ref.getStore().getComponent(ref, UUIDComponent.getComponentType());
        FancyPlayerImpl fp = (FancyPlayerImpl) playerService.getByUUID(uuidComponent.getUuid());
        if (fp == null) {
            return;
        }

        if (FancyCorePlugin.get().getConfig().shouldJoinAtSpawn()) {
            Location spawnLocation = SpawnService.get().getSpawnLocation();
            if (spawnLocation != null) {
                TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());

                Teleport teleport = new Teleport(event.getPlayer().getWorld(), spawnLocation.positionVec(), spawnLocation.rotationVec());
                store.addComponent(ref, Teleport.getComponentType(), teleport);
            }
        }

        UUID joiningPlayerUuid = fp.getData().getUUID();
        World playerWorld = event.getPlayer().getWorld();
        PlayerRef joiningPlayerRef = fp.getPlayer();
        
        if (playerWorld != null && joiningPlayerRef != null) {
            playerWorld.execute(() -> {
                // Reapply vanish state if player was vanished - hide them from all other players
                if (fp.getData().isVanished()) {
                    for (World world : Universe.get().getWorlds().values()) {
                        Collection<PlayerRef> players = world.getPlayerRefs();
                        for (PlayerRef otherPlayerRef : players) {
                            if (!otherPlayerRef.getUuid().equals(joiningPlayerUuid)) {
                                otherPlayerRef.getHiddenPlayersManager().hidePlayer(joiningPlayerUuid);
                            }
                        }
                    }
                }
                
                // Hide all other vanished players from the newly joining player
                // Iterate through all worlds to find vanished players
                for (World world : Universe.get().getWorlds().values()) {
                    Collection<PlayerRef> players = world.getPlayerRefs();
                    for (PlayerRef otherPlayerRef : players) {
                        if (!otherPlayerRef.getUuid().equals(joiningPlayerUuid)) {
                            FancyPlayer otherFancyPlayer = playerService.getByUUID(otherPlayerRef.getUuid());
                            if (otherFancyPlayer != null && otherFancyPlayer.getData().isVanished()) {
                                joiningPlayerRef.getHiddenPlayersManager().hidePlayer(otherPlayerRef.getUuid());
                            }
                        }
                    }
                }
            });
        }

        // Reapply fly state if player had fly enabled
        if (fp.getData().isFlying()) {
            Ref<EntityStore> playerEntityRef = ref;
            Store<EntityStore> playerEntityStore = store;
            PlayerRef playerRef = fp.getPlayer();
            if (playerWorld != null && playerEntityRef != null && playerEntityRef.isValid() && playerRef != null) {
                playerWorld.execute(() -> {
                    MovementManager movementManager = playerEntityStore.getComponent(playerEntityRef, MovementManager.getComponentType());
                    if (movementManager != null) {
                        movementManager.getSettings().canFly = true;
                        movementManager.update(playerRef.getPacketHandler());
                    }
                });
            }
        }

        // show scoreboard
        String defaultScoreboardPageName = FancyCorePlugin.get().getConfig().getDefaultScoreboardPageName();
        if (defaultScoreboardPageName != null && !defaultScoreboardPageName.isEmpty()) {
            ScoreboardPage defaultPage = ScoreboardService.get().getPage(defaultScoreboardPageName);

            if (defaultPage != null) {
                FancyCorePlugin.get().getScoreboardServiceImpl().attachScoreboard(fp, defaultPage);
            } else {
                FancyCorePlugin.get().getFancyLogger().warn(
                        "The configured '" + defaultScoreboardPageName + "' scoreboard page does not exist → scoreboard not attached for " + fp.getPlayer().getUsername());
            }
        } else {
            FancyCorePlugin.get().getFancyLogger().warn(
                    "No default scoreboard page configured (empty config) → scoreboard not attached"
            );
        }
    }

    public static void onAddPlayerToWorld(AddPlayerToWorldEvent event) {
        event.setBroadcastJoinMessage(false);
    }
}
