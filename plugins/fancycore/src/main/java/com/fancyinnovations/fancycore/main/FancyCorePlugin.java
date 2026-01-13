package com.fancyinnovations.fancycore.main;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.FancyCoreConfig;
import com.fancyinnovations.fancycore.api.chat.ChatService;
import com.fancyinnovations.fancycore.api.chat.ChatStorage;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.economy.CurrencyStorage;
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
import com.fancyinnovations.fancycore.commands.server.UpdatePluginCMD;
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
import com.fancyinnovations.fancycore.translations.TranslationService;
import com.fancyinnovations.versionchecker.FancySpacesVersionFetcher;
import com.fancyinnovations.versionchecker.VersionChecker;
import com.google.gson.Gson;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;

import javax.annotation.Nonnull;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FancyCorePlugin extends JavaPlugin implements FancyCore {

    public static final Gson GSON = new Gson();
    private static FancyCorePlugin INSTANCE;

    private final ExtendedFancyLogger fancyLogger;
    private final ScheduledExecutorService threadPool;

    private FancyCoreConfig fancyCoreConfig;
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

        versionChecker = new VersionChecker(fancyLogger, "FancyCore", new FancySpacesVersionFetcher("fc"));

        pluginMetrics = new PluginMetrics();

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
        permissionService = new PermissionServiceImpl();

        chatStorage = new ChatJsonStorage();
        chatService = new ChatServiceImpl();

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

        fancyLogger.info("FancyCore has been started.");
    }

    @Override
    protected void shutdown() {
        fancyLogger.info("FancyCore is shutting down...");

        threadPool.shutdown();

        savePlayersRunnable.run();

        fancyLogger.info("FancyCore has been shut down.");
    }

    public void registerCommands() {
        // TODO (HTEA): register commands properly

        new UpdatePluginCMD();
    }

    public void registerListeners() {
        // TODO (HTEA): register listeners properly
        EventRegistry eventRegistry = this.getEventRegistry();
        eventRegistry.register(PlayerConnectEvent.class, PlayerJoinListener::onPlayerJoin);

        new PlayerLeaveListener();
        new PlayerChatListener();
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    @Override
    public FancyCoreConfig getConfig() {
        return fancyCoreConfig;
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
}
