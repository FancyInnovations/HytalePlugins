package com.fancyinnovations.fancycore.main;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.FancyCoreConfig;
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
import com.fancyinnovations.fancycore.config.FancyCoreConfigImpl;
import com.fancyinnovations.fancycore.economy.service.CurrencyServiceImpl;
import com.fancyinnovations.fancycore.economy.storage.json.CurrencyJsonStorage;
import com.fancyinnovations.fancycore.events.EventServiceImpl;
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
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FancyCorePlugin implements FancyCore {

    private static FancyCorePlugin INSTANCE;

    private final ExtendedFancyLogger fancyLogger;
    private final ScheduledExecutorService threadPool;

    private final FancyCoreConfig fancyCoreConfig;
    private final VersionChecker versionChecker;

    private final PluginMetrics pluginMetrics;

    private final EventService eventService;
    private final PlaceholderService placeholderService;

    private final TranslationService translationService;

    private final FancyPlayerStorage playerStorage;
    private final FancyPlayerService playerService;
    private final SavePlayersRunnable savePlayersRunnable;
    private final CleanUpPlayerCacheRunnable cleanUpPlayerCacheRunnable;

    private final PunishmentStorage punishmentStorage;
    private final PunishmentService punishmentService;

    private final CurrencyStorage currencyStorage;
    private final CurrencyService currencyService;

    private final PermissionStorage permissionStorage;
    private final PermissionService permissionService;

    public FancyCorePlugin() {
        INSTANCE = this;
        FancyCore.InstanceHolder.setInstance(INSTANCE);

        Appender consoleAppender = new ConsoleAppender("[{loggerName}] ({threadName}) {logLevel}: {message}");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        File logsFile = new File("plugins/FancyCore/logs/FC-logs-" + date + ".txt");
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
    }

    public static FancyCorePlugin get() {
        return INSTANCE;
    }

    public void onEnable() {
        fancyLogger.info("FancyCore is enabling...");

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

        fancyLogger.info("FancyCore has been enabled.");
    }

    public void onDisable() {
        fancyLogger.info("FancyCore is disabling...");

        threadPool.shutdown();

        savePlayersRunnable.run();

        fancyLogger.info("FancyCore has been disabled.");
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    @Override
    public FancyCoreConfig getConfig() {
        return fancyCoreConfig;
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
}
