package com.fancyinnovations.fancycore.main;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.FancyCoreConfig;
import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.chat.ChatStorage;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.economy.CurrencyStorage;
import com.fancyinnovations.fancycore.api.events.server.ServerStartedEvent;
import com.fancyinnovations.fancycore.api.events.server.ServerStoppedEvent;
import com.fancyinnovations.fancycore.api.events.service.EventService;
import com.fancyinnovations.fancycore.api.inventory.BackpacksService;
import com.fancyinnovations.fancycore.api.inventory.BackpacksStorage;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.inventory.KitsStorage;
import com.fancyinnovations.fancycore.api.moderation.PunishmentService;
import com.fancyinnovations.fancycore.api.moderation.PunishmentStorage;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.permissions.PermissionStorage;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerStorage;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardService;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardStorage;
import com.fancyinnovations.fancycore.api.teleport.*;
import com.fancyinnovations.fancycore.chat.service.ChatServiceImpl;
import com.fancyinnovations.fancycore.chat.storage.json.ChatJsonStorage;
import com.fancyinnovations.fancycore.commands.chat.BroadcastCMD;
import com.fancyinnovations.fancycore.commands.chat.ChatColorCMD;
import com.fancyinnovations.fancycore.commands.chat.chatroom.ChatRoomCMD;
import com.fancyinnovations.fancycore.commands.chat.message.*;
import com.fancyinnovations.fancycore.commands.economy.*;
import com.fancyinnovations.fancycore.commands.economy.currency.CurrencyCMD;
import com.fancyinnovations.fancycore.commands.fancycore.FancyCoreCMD;
import com.fancyinnovations.fancycore.commands.inventory.*;
import com.fancyinnovations.fancycore.commands.moderation.*;
import com.fancyinnovations.fancycore.commands.moderation.punishments.PunishmentsCMD;
import com.fancyinnovations.fancycore.commands.moderation.reports.ReportCMD;
import com.fancyinnovations.fancycore.commands.moderation.reports.ReportsCMD;
import com.fancyinnovations.fancycore.commands.permissions.OpCMD;
import com.fancyinnovations.fancycore.commands.permissions.groups.GroupCMD;
import com.fancyinnovations.fancycore.commands.permissions.player.PermissionsCMD;
import com.fancyinnovations.fancycore.commands.player.*;
import com.fancyinnovations.fancycore.commands.scoreboard.ScoreboardCMD;
import com.fancyinnovations.fancycore.commands.teleport.*;
import com.fancyinnovations.fancycore.config.FancyCoreConfigImpl;
import com.fancyinnovations.fancycore.economy.service.CurrencyServiceImpl;
import com.fancyinnovations.fancycore.economy.storage.json.CurrencyJsonStorage;
import com.fancyinnovations.fancycore.events.EventServiceImpl;
import com.fancyinnovations.fancycore.inventory.service.BackpacksServiceImpl;
import com.fancyinnovations.fancycore.inventory.service.KitsServiceImpl;
import com.fancyinnovations.fancycore.inventory.storage.json.BackpacksJsonStorage;
import com.fancyinnovations.fancycore.inventory.storage.json.KitsJsonStorage;
import com.fancyinnovations.fancycore.listeners.PlayerChatListener;
import com.fancyinnovations.fancycore.listeners.PlayerJoinListener;
import com.fancyinnovations.fancycore.listeners.PlayerLeaveListener;
import com.fancyinnovations.fancycore.metrics.PluginMetrics;
import com.fancyinnovations.fancycore.moderation.service.PunishmentServiceImpl;
import com.fancyinnovations.fancycore.moderation.storage.json.PunishmentJsonStorage;
import com.fancyinnovations.fancycore.permissions.FancyCorePermissionProvider;
import com.fancyinnovations.fancycore.permissions.service.PermissionServiceImpl;
import com.fancyinnovations.fancycore.permissions.storage.json.PermissionJsonStorage;
import com.fancyinnovations.fancycore.placeholders.PlaceholderServiceImpl;
import com.fancyinnovations.fancycore.placeholders.builtin.BuiltInPlaceholderProviders;
import com.fancyinnovations.fancycore.player.service.CleanUpPlayerCacheRunnable;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import com.fancyinnovations.fancycore.player.storage.SavePlayersRunnable;
import com.fancyinnovations.fancycore.player.storage.json.FancyPlayerJsonStorage;
import com.fancyinnovations.fancycore.scoreboard.service.ScoreboardServiceImpl;
import com.fancyinnovations.fancycore.scoreboard.storage.ScoreboardJsonStorage;
import com.fancyinnovations.fancycore.teleport.service.SpawnServiceImpl;
import com.fancyinnovations.fancycore.teleport.service.TeleportRequestServiceImpl;
import com.fancyinnovations.fancycore.teleport.service.WarpServiceImpl;
import com.fancyinnovations.fancycore.teleport.storage.json.SpawnJsonStorage;
import com.fancyinnovations.fancycore.teleport.storage.json.WarpJsonStorage;
import com.fancyinnovations.fancycore.translations.TranslationService;
import com.fancyinnovations.versionchecker.FancySpacesVersionFetcher;
import com.fancyinnovations.versionchecker.VersionChecker;
import com.fancyinnovations.versionchecker.VersionFetcher;
import com.google.gson.Gson;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.event.events.player.*;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.permissions.provider.PermissionProvider;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import javax.annotation.Nonnull;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

public class FancyCorePlugin extends JavaPlugin implements FancyCore {

    public static final Gson GSON = new Gson();
    private static FancyCorePlugin INSTANCE;

    private final ExtendedFancyLogger fancyLogger;
    private final ScheduledExecutorService threadPool;

    private FancyCoreConfig fancyCoreConfig;
    private VersionFetcher versionFetcher;
    private VersionChecker versionChecker;

    private PluginMetrics pluginMetrics;

    private EventService eventService;
    private PlaceholderService placeholderService;

    private TranslationService translationService;

    private FancyPlayerStorage playerStorage;
    private FancyPlayerService playerService;
    private SavePlayersRunnable savePlayersRunnable;
    private CleanUpPlayerCacheRunnable cleanUpPlayerCacheRunnable;

    private PunishmentStorage punishmentStorage;
    private PunishmentService punishmentService;

    private CurrencyStorage currencyStorage;
    private CurrencyService currencyService;

    private PermissionStorage permissionStorage;
    private PermissionService permissionService;

    private ChatStorage chatStorage;
    private ChatService chatService;

    private TeleportRequestService teleportRequestService;
    private SpawnStorage spawnStorage;
    private SpawnService spawnService;
    private WarpStorage warpStorage;
    private WarpService warpService;

    private KitsStorage kitsStorage;
    private KitsService kitsService;

    private BackpacksStorage backpacksStorage;
    private BackpacksService backpacksService;

    private ScoreboardStorage scoreboardStorage;
    private ScoreboardService scoreboardService;

    public FancyCorePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        FancyCore.InstanceHolder.setInstance(INSTANCE);

        Appender consoleAppender = new ConsoleAppender("[{loggerName}] ({threadName}) {logLevel}: {message}");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        File logsFile = new File("mods/FancyCore/logs/FC-logs-" + date + ".txt");
        if (!logsFile.exists()) {
            try {
                logsFile.getParentFile().mkdirs();
                logsFile.createNewFile();
            } catch (Exception e) {
                // Logger not yet initialized, use System.err as fallback during bootstrap
                System.err.println("[FancyCore] Failed to create log file: " + e.getMessage());
            }
        }
        JsonAppender jsonAppender = new JsonAppender(false, false, true, logsFile.getPath());
        fancyLogger = new ExtendedFancyLogger(
                "FancyCore",
                LogLevel.INFO,
                List.of(consoleAppender, jsonAppender),
                List.of()
        );

        threadPool = Executors.newScheduledThreadPool(4, r -> {
            Thread thread = new Thread(r);
            thread.setName("FancyCore-ThreadPool-" + thread.threadId());
            return thread;
        });
    }

    public static FancyCorePlugin get() {
        return INSTANCE;
    }

    @Override
    protected void setup() {
        fancyLogger.info("Setting up FancyCore...");

        // load config
        fancyCoreConfig = new FancyCoreConfigImpl();
        ((FancyCoreConfigImpl) fancyCoreConfig).init();

        versionFetcher = new FancySpacesVersionFetcher("fc");
        versionChecker = new VersionChecker(fancyLogger, "FancyCore", versionFetcher);

        pluginMetrics = new PluginMetrics("4bcf8d05-9d69-4574-9d81-96ec0ec2894c");

        eventService = new EventServiceImpl(fancyCoreConfig);
        placeholderService = new PlaceholderServiceImpl();

        translationService = new TranslationService();

        playerStorage = new FancyPlayerJsonStorage();
        playerService = new FancyPlayerServiceImpl();
        savePlayersRunnable = new SavePlayersRunnable();
        cleanUpPlayerCacheRunnable = new CleanUpPlayerCacheRunnable();

        punishmentStorage = new PunishmentJsonStorage();
        punishmentService = new PunishmentServiceImpl();

        currencyStorage = new CurrencyJsonStorage();
        currencyService = new CurrencyServiceImpl();

        permissionStorage = new PermissionJsonStorage();
        permissionService = new PermissionServiceImpl(permissionStorage);

        chatStorage = new ChatJsonStorage();
        chatService = new ChatServiceImpl();

        teleportRequestService = new TeleportRequestServiceImpl();
        spawnStorage = new SpawnJsonStorage();
        spawnService = new SpawnServiceImpl(spawnStorage);
        warpStorage = new WarpJsonStorage();
        warpService = new WarpServiceImpl(warpStorage);

        kitsStorage = new KitsJsonStorage();
        kitsService = new KitsServiceImpl(kitsStorage);

        backpacksStorage = new BackpacksJsonStorage();
        backpacksService = new BackpacksServiceImpl(backpacksStorage);

        scoreboardStorage = new ScoreboardJsonStorage();
        scoreboardService = new ScoreboardServiceImpl(scoreboardStorage);

        SeedDefaultData.seed();

        fancyLogger.info("FancyCore has been set up.");
    }

    @Override
    public void start() {
        fancyLogger.info("FancyCore is starting...");


        // set log level from config
        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(fancyCoreConfig.getLogLevel());
        } catch (IllegalArgumentException e) {
            logLevel = LogLevel.INFO;
        }
        fancyLogger.setCurrentLevel(logLevel);

        // check if latest version is running
        versionChecker.checkForConsole();

        // TODO enable this once FA integration is configured
        // versionChecker.checkPluginVersionChanged(apiClient, "fc");

        // register built-in placeholder providers
        BuiltInPlaceholderProviders.registerAll();

        // start player schedulers
        savePlayersRunnable.schedule();
        cleanUpPlayerCacheRunnable.schedule();

        // register metrics
        pluginMetrics.register();

        // register commands and listeners
        registerCommands();
        registerListeners();

        // register permission provider
        if (!fancyCoreConfig.disablePermissionProvider()) {
            for (PermissionProvider permissionProvider : new ArrayList<>(PermissionsModule.get().getProviders())) {
                PermissionsModule.get().removeProvider(permissionProvider);
            }
            PermissionsModule.get().addProvider(new FancyCorePermissionProvider());
        }

        new ServerStartedEvent().fire();

        fancyLogger.info("FancyCore has been started.");
    }

    @Override
    protected void shutdown() {
        fancyLogger.info("FancyCore is shutting down...");

        // Cancel all scheduled tasks before shutdown
        if (savePlayersRunnable != null) {
            ScheduledFuture<?> saveSchedule = savePlayersRunnable.getSchedule();
            if (saveSchedule != null && !saveSchedule.isCancelled()) {
                saveSchedule.cancel(false);
            }
            savePlayersRunnable.run();
        }

        if (cleanUpPlayerCacheRunnable != null) {
            ScheduledFuture<?> cleanupSchedule = cleanUpPlayerCacheRunnable.getSchedule();
            if (cleanupSchedule != null && !cleanupSchedule.isCancelled()) {
                cleanupSchedule.cancel(false);
            }
        }

        if (scoreboardService != null && scoreboardService instanceof ScoreboardServiceImpl) {
            ScheduledFuture<?> scoreboardSchedule = ((ScoreboardServiceImpl) scoreboardService).getUpdateSchedule();
            if (scoreboardSchedule != null && !scoreboardSchedule.isCancelled()) {
                scoreboardSchedule.cancel(false);
            }
        }

        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                fancyLogger.warn("Thread pool did not terminate within 5 seconds, forcing shutdown...");
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    fancyLogger.error("Thread pool did not terminate after forced shutdown");
                }
            }
        } catch (InterruptedException e) {
            fancyLogger.warn("Thread pool shutdown interrupted, forcing shutdown...");
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        new ServerStoppedEvent().fire();

        fancyLogger.info("FancyCore has been shut down.");
    }

    public void registerCommands() {
        // server
        CommandManager.get().register(new FancyCoreCMD());

        // chat
        CommandManager.get().register(new ChatRoomCMD());
        CommandManager.get().register(new MessageCMD());
        CommandManager.get().register(new ReplyCMD());
        CommandManager.get().register(new IgnoreCMD());
        CommandManager.get().register(new UnignoreCMD());
        CommandManager.get().register(new ToggleMessagesCMD());
        CommandManager.get().register(new ChatColorCMD());
        CommandManager.get().register(new BroadcastCMD());

        // teleport
        CommandManager.get().register(new TeleportCMD());
        CommandManager.get().register(new TeleportHereCMD());
        CommandManager.get().register(new TeleportAllCMD());
        CommandManager.get().register(new TeleportPosCMD());
        CommandManager.get().register(new TeleportRequestCMD());
        CommandManager.get().register(new TeleportAcceptCMD());
        CommandManager.get().register(new TeleportDenyCMD());
        CommandManager.get().register(new TeleportBackCMD());
        CommandManager.get().register(new TeleportDeathBackCMD());
        CommandManager.get().register(new SwitchWorldCMD());
        CommandManager.get().register(new SwitchServerCMD());
        CommandManager.get().register(new SetSpawnCMD());
        CommandManager.get().register(new SpawnCMD());
        CommandManager.get().register(new SetHomeCMD());
        CommandManager.get().register(new DeleteHomeCMD());
        CommandManager.get().register(new HomeCMD());
        CommandManager.get().register(new ListHomesCMD());
        CommandManager.get().register(new SetWarpCMD());
        CommandManager.get().register(new DeleteWarpCMD());
        CommandManager.get().register(new WarpCMD());
        CommandManager.get().register(new ListWarpsCMD());

        // player
        CommandManager.get().register(new PlayerListCMD());
        CommandManager.get().register(new PlaytimeCMD());
        CommandManager.get().register(new GameModeCMD());
        CommandManager.get().register(new GMACMD());
        CommandManager.get().register(new GMCCMD());
        CommandManager.get().register(new FlyCMD());
        CommandManager.get().register(new SpeedCMD());
        CommandManager.get().register(new HealCMD());
        CommandManager.get().register(new FeedCMD());
        CommandManager.get().register(new GodCMD());
        CommandManager.get().register(new VanishCMD());
        CommandManager.get().register(new SeenCMD());
        CommandManager.get().register(new NearCMD());
        CommandManager.get().register(new PingCMD());

        // permission
        CommandManager.get().register(new PermissionsCMD());
        CommandManager.get().register(new GroupCMD());
        CommandManager.get().register(new OpCMD());

        // kits
        CommandManager.get().register(new CreateKitCMD());
        CommandManager.get().register(new DeleteKitCMD());
        CommandManager.get().register(new KitCMD());
        CommandManager.get().register(new ListKitsCMD());

        // utilities
        CommandManager.get().register(new ClearInventoryCMD());
        CommandManager.get().register(new OpenInventoryCMD());

        // backpacks
        CommandManager.get().register(new BackpackCMD());
        CommandManager.get().register(new CreateBackpackCMD());
        CommandManager.get().register(new DeleteBackpackCMD());
        CommandManager.get().register(new ListBackpacksCMD());

        // economy
        CommandManager.get().register(new CurrencyCMD());
        CommandManager.get().register(new BalanceCMD());
        CommandManager.get().register(new PayCMD());
        CommandManager.get().register(new AddMoneyCMD());
        CommandManager.get().register(new RemoveMoneyCMD());
        CommandManager.get().register(new SetMoneyCMD());
        CommandManager.get().register(new BalanceTopCMD());

        // moderation
        CommandManager.get().register(new ReportCMD());
        CommandManager.get().register(new ReportsCMD());
        CommandManager.get().register(new WarnCMD());
        CommandManager.get().register(new KickCMD());
        CommandManager.get().register(new MuteCMD());
        CommandManager.get().register(new TempMuteCMD());
        CommandManager.get().register(new UnmuteCMD());
        CommandManager.get().register(new BanCMD());
        CommandManager.get().register(new TempBanCMD());
        CommandManager.get().register(new UnbanCMD());
        CommandManager.get().register(new PunishmentsCMD());

        // scoreboards
        CommandManager.get().register(new ScoreboardCMD());
    }

    public void registerListeners() {
        EventRegistry eventRegistry = this.getEventRegistry();
        eventRegistry.registerGlobal(PlayerConnectEvent.class, PlayerJoinListener::onPlayerConnect);
        eventRegistry.registerGlobal(PlayerReadyEvent.class, PlayerJoinListener::onPlayerReady);
        eventRegistry.registerGlobal(AddPlayerToWorldEvent.class, PlayerJoinListener::onAddPlayerToWorld);
        eventRegistry.registerGlobal(PlayerDisconnectEvent.class, PlayerLeaveListener::onPlayerLeave);

        Function<CompletableFuture<PlayerChatEvent>, CompletableFuture<PlayerChatEvent>> handler = future ->
                future.thenApply(event -> {
                    try {
                        PlayerChatListener.onPlayerChat(event);
                    } catch (Exception e) {
                        fancyLogger.error("Error handling player chat event", ThrowableProperty.of(e));
                    }
                    return event;
                });
        eventRegistry.registerAsyncGlobal(PlayerChatEvent.class, handler);
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    @Override
    public FancyCoreConfig getConfig() {
        return fancyCoreConfig;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public VersionChecker getVersionChecker() {
        return versionChecker;
    }

    @Override
    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    @Override
    public EventService getEventService() {
        return eventService;
    }

    @Override
    public PlaceholderService getPlaceholderService() {
        return placeholderService;
    }

    @Override
    public com.fancyinnovations.fancycore.api.translations.TranslationService getTranslationService() {
        return translationService;
    }

    @Override
    public FancyPlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    @Override
    public FancyPlayerService getPlayerService() {
        return playerService;
    }

    @Override
    public PunishmentStorage getPunishmentStorage() {
        return punishmentStorage;
    }

    @Override
    public PunishmentService getPunishmentService() {
        return punishmentService;
    }

    @Override
    public CurrencyStorage getCurrencyStorage() {
        return currencyStorage;
    }

    @Override
    public CurrencyService getCurrencyService() {
        return currencyService;
    }

    @Override
    public PermissionStorage getPermissionStorage() {
        return permissionStorage;
    }

    @Override
    public PermissionService getPermissionService() {
        return permissionService;
    }

    @Override
    public ChatStorage getChatStorage() {
        return chatStorage;
    }

    @Override
    public ChatService getChatService() {
        return chatService;
    }

    @Override
    public TeleportRequestService getTeleportRequestService() {
        return teleportRequestService;
    }

    @Override
    public SpawnService getSpawnService() {
        return spawnService;
    }

    @Override
    public SpawnStorage getSpawnStorage() {
        return spawnStorage;
    }

    @Override
    public WarpService getWarpService() {
        return warpService;
    }

    @Override
    public WarpStorage getWarpStorage() {
        return warpStorage;
    }

    @Override
    public KitsStorage getKitsStorage() {
        return kitsStorage;
    }

    @Override
    public KitsService getKitsService() {
        return kitsService;
    }

    @Override
    public BackpacksStorage getBackpacksStorage() {
        return backpacksStorage;
    }

    @Override
    public BackpacksService getBackpacksService() {
        return backpacksService;
    }

    @Override
    public ScoreboardStorage getScoreboardStorage() {
        return scoreboardStorage;
    }

    @Override
    public ScoreboardService getScoreboardService() {
        return scoreboardService;
    }

    public ScoreboardServiceImpl getScoreboardServiceImpl() {
        return (ScoreboardServiceImpl) scoreboardService;
    }
}
