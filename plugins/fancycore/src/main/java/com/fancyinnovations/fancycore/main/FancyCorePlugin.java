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
import com.fancyinnovations.fancycore.api.moderation.PunishmentService;
import com.fancyinnovations.fancycore.api.moderation.PunishmentStorage;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.permissions.PermissionStorage;
import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.player.FancyPlayerStorage;
import com.fancyinnovations.fancycore.chat.service.ChatServiceImpl;
import com.fancyinnovations.fancycore.chat.storage.json.ChatJsonStorage;
import com.fancyinnovations.fancycore.commands.chat.chatroom.ChatRoomCMD;
import com.fancyinnovations.fancycore.commands.chat.message.*;
import com.fancyinnovations.fancycore.commands.fancycore.FancyCoreCMD;
import com.fancyinnovations.fancycore.commands.permissions.CheckPermissionCMD;
import com.fancyinnovations.fancycore.commands.permissions.groups.GroupCMD;
import com.fancyinnovations.fancycore.commands.player.PlayerListCMD;
import com.fancyinnovations.fancycore.commands.teleport.*;
import com.fancyinnovations.fancycore.config.FancyCoreConfigImpl;
import com.fancyinnovations.fancycore.economy.service.CurrencyServiceImpl;
import com.fancyinnovations.fancycore.economy.storage.json.CurrencyJsonStorage;
import com.fancyinnovations.fancycore.events.EventServiceImpl;
import com.fancyinnovations.fancycore.listeners.PlayerChatListener;
import com.fancyinnovations.fancycore.listeners.PlayerJoinListener;
import com.fancyinnovations.fancycore.listeners.PlayerLeaveListener;
import com.fancyinnovations.fancycore.metrics.PluginMetrics;
import com.fancyinnovations.fancycore.moderation.service.PunishmentServiceImpl;
import com.fancyinnovations.fancycore.moderation.storage.json.PunishmentJsonStorage;
import com.fancyinnovations.fancycore.permissions.service.PermissionServiceImpl;
import com.fancyinnovations.fancycore.permissions.storage.json.PermissionJsonStorage;
import com.fancyinnovations.fancycore.placeholders.PlaceholderServiceImpl;
import com.fancyinnovations.fancycore.placeholders.builtin.BuiltInPlaceholderProviders;
import com.fancyinnovations.fancycore.player.service.CleanUpPlayerCacheRunnable;
import com.fancyinnovations.fancycore.player.service.FancyPlayerServiceImpl;
import com.fancyinnovations.fancycore.player.storage.SavePlayersRunnable;
import com.fancyinnovations.fancycore.player.storage.json.FancyPlayerJsonStorage;
import com.fancyinnovations.fancycore.teleport.service.TeleportRequestServiceImpl;
import com.fancyinnovations.fancycore.teleport.storage.SpawnLocationStorage;
import com.fancyinnovations.fancycore.teleport.storage.WarpStorage;
import com.fancyinnovations.fancycore.translations.TranslationService;
import com.fancyinnovations.versionchecker.FancySpacesVersionFetcher;
import com.fancyinnovations.versionchecker.VersionChecker;
import com.fancyinnovations.versionchecker.VersionFetcher;
import com.google.gson.Gson;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.event.events.player.*;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

    private com.fancyinnovations.fancycore.api.teleport.TeleportRequestService teleportRequestService;
    private SpawnLocationStorage spawnLocationStorage;
    private WarpStorage warpStorage;

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
                e.printStackTrace();
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

        fancyCoreConfig = new FancyCoreConfigImpl();

        versionFetcher = new FancySpacesVersionFetcher("fc");
        versionChecker = new VersionChecker(fancyLogger, "FancyCore", versionFetcher);

        pluginMetrics = new PluginMetrics("4bcf8d05-9d69-4574-9d81-96ec0ec2894c");

        eventService = new EventServiceImpl();
        placeholderService = new PlaceholderServiceImpl();
        BuiltInPlaceholderProviders.registerAll();

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
        spawnLocationStorage = new SpawnLocationStorage();
        warpStorage = new WarpStorage();

        SeedDefaultData.seed();

        fancyLogger.info("FancyCore has been set up.");
    }

    @Override
    public void start() {
        fancyLogger.info("FancyCore is starting...");

        // load config
        ((FancyCoreConfigImpl) fancyCoreConfig).init();

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


        // start player schedulers
        savePlayersRunnable.schedule();
        cleanUpPlayerCacheRunnable.schedule();

        // register metrics
        pluginMetrics.register();

        // register commands and listeners
        registerCommands();
        registerListeners();

        new ServerStartedEvent().fire();

        fancyLogger.info("FancyCore has been started.");
    }

    @Override
    protected void shutdown() {
        fancyLogger.info("FancyCore is shutting down...a");

        savePlayersRunnable.run();

        threadPool.shutdown();

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
        CommandManager.get().register(new SetSpawnCMD());
        CommandManager.get().register(new SpawnCMD());
        CommandManager.get().register(new SetHomeCMD());
        CommandManager.get().register(new DeleteHomeCMD());
        CommandManager.get().register(new HomeCMD());
        CommandManager.get().register(new ListHomesCMD());
        CommandManager.get().register(new CreateWarpCMD());
        CommandManager.get().register(new DeleteWarpCMD());
        CommandManager.get().register(new WarpCMD());
        CommandManager.get().register(new ListWarpsCMD());

        // player
        CommandManager.get().register(new PlayerListCMD());

        // permission
        CommandManager.get().register(new GroupCMD());
        CommandManager.get().register(new CheckPermissionCMD());
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

    public TranslationService getTranslationService() {
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
    public com.fancyinnovations.fancycore.api.teleport.TeleportRequestService getTeleportRequestService() {
        return teleportRequestService;
    }

    public SpawnLocationStorage getSpawnLocationStorage() {
        return spawnLocationStorage;
    }

    public WarpStorage getWarpStorage() {
        return warpStorage;
    }
}
