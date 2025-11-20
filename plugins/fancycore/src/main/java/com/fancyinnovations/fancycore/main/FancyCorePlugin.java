package com.fancyinnovations.fancycore.main;

import com.fancyinnovations.fancycore.api.FancyCore;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FancyCorePlugin implements FancyCore {

    private static FancyCorePlugin INSTANCE;

    private final ExtendedFancyLogger fancyLogger;

    public FancyCorePlugin() {
        INSTANCE = this;

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
    }

    public static FancyCorePlugin get() {
        return INSTANCE;
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }
}
