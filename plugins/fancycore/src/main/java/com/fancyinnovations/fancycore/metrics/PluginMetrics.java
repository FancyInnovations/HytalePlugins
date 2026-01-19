package com.fancyinnovations.fancycore.metrics;

import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.io.ServerManager;
import de.oliver.fancyanalytics.sdk.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PluginMetrics {

    private static final FancyCorePlugin plugin = FancyCorePlugin.get();
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "FancyAnalyticsAPI"));
    private static final String BASE_URL = "https://api.fancyanalytics.net";

    private final ApiClient apiClient;
    private final String projectId;
    private final List<MetricSupplier<?>> metrics;
    private final String senderId;

    public PluginMetrics(String projectId) {
        this.projectId = projectId;
        this.apiClient = new ApiClient(BASE_URL, "", "");
        this.metrics = new ArrayList<>();
        this.senderId = UUID.randomUUID().toString();
    }

    public void register() {
        this.metrics.add(new MetricSupplier<String>("fancycore_version", this::pluginVersion));
        this.metrics.add(new MetricSupplier<String>("server_version", this::serverVersion));
        this.metrics.add(new MetricSupplier<String>("disabled_permission_provider", this::disabledPermissionProvider));

        this.metrics.add(new MetricSupplier<Double>("online_players", this::onlinePlayers));
        this.metrics.add(new MetricSupplier<String>("server_size_category", this::serverSizeCategory));

        this.metrics.add(new MetricSupplier<Double>("total_amount_players", this::totalAmountPlayers));
        this.metrics.add(new MetricSupplier<Double>("total_amount_punishments", this::totalAmountPunishments));
        this.metrics.add(new MetricSupplier<Double>("total_amount_reports", this::totalAmountReports));
        this.metrics.add(new MetricSupplier<Double>("total_amount_groups", this::totalAmountGroups));
        this.metrics.add(new MetricSupplier<Double>("total_amount_kits", this::totalAmountKits));
        this.metrics.add(new MetricSupplier<Double>("total_amount_backpacks", this::totalAmountBackpacks));
        this.metrics.add(new MetricSupplier<Double>("total_amount_scoreboard_pages", this::totalAmountScoreboardPages));

        EXECUTOR.scheduleAtFixedRate(this::send, 5, 30, TimeUnit.SECONDS);
    }

    public void send() {
        if (FancyCorePlugin.get().getConfig().isAnalyticsDisabled()) {
            return;
        }

        de.oliver.fancyanalytics.sdk.records.Record r = new de.oliver.fancyanalytics.sdk.records.Record(senderId, projectId, System.currentTimeMillis(), new HashMap<>());
        for (MetricSupplier<?> metric : metrics) {
            r.withEntry(metric.name(), metric.valueSupplier().get());
        }

        apiClient.getRecordService().createRecord(projectId, r);
    }

    private double totalAmountPlayers() {
        return plugin.getPlayerStorage().countPlayers();
    }

    private double onlinePlayers() {
        return plugin.getPlayerService().getOnlinePlayers().size();
    }

    private String serverSizeCategory() {
        if (onlinePlayers() == 0) {
            return "empty";
        } else if (onlinePlayers() <= 25) {
            return "small";
        } else if (onlinePlayers() <= 100) {
            return "medium";
        } else if (onlinePlayers() <= 500) {
            return "large";
        } else if (onlinePlayers() > 500) {
            return "very_large";
        }

        return "unknown";
    }

    private String serverVersion() {
        return ServerManager.get().getManifest().getVersion().toString();
    }

    private String pluginVersion() {
        return FancyCorePlugin.get().getManifest().getVersion().toString();
    }

    private String disabledPermissionProvider() {
        return String.valueOf(FancyCorePlugin.get().getConfig().disablePermissionProvider());
    }

    private double totalAmountPunishments() {
        return plugin.getPunishmentStorage().countTotalPunishments();
    }

    private double totalAmountReports() {
        return plugin.getPunishmentStorage().countTotalReports();
    }

    private double totalAmountGroups() {
        return plugin.getPermissionStorage().countGroups();
    }

    private double totalAmountKits() {
        return plugin.getKitsStorage().countKits();
    }

    private double totalAmountBackpacks() {
        return plugin.getBackpacksStorage().countBackpacks();
    }

    private double totalAmountScoreboardPages() {
        return plugin.getScoreboardStorage().countPages();
    }

    /**
     * Registers a numerical metric using the provided MetricSupplier.
     * The metric will be stored and managed by the FancyAnalyticsAPI instance.
     *
     * @param metric the MetricSupplier containing the name and value supplier of the numeric metric to be registered
     * @throws IllegalArgumentException if a metric with the same name already exists
     */
    public void registerNumberMetric(MetricSupplier<Double> metric) {
        for (MetricSupplier<?> m : metrics) {
            if (m.name().equals(metric.name())) {
                throw new IllegalArgumentException("Metric with name " + metric.name() + " already exists");
            }
        }

        metrics.add(metric);
    }

    /**
     * Registers a numerical array metric using the provided MetricSupplier.
     * The metric will be stored and managed by the FancyAnalyticsAPI instance.
     *
     * @param metric the MetricSupplier containing the name and value supplier of the numeric array metric to be registered
     * @throws IllegalArgumentException if a metric with the same name already exists
     */
    public void registerNumberArrayMetric(MetricSupplier<Double[]> metric) {
        for (MetricSupplier<?> m : metrics) {
            if (m.name().equals(metric.name())) {
                throw new IllegalArgumentException("Metric with name " + metric.name() + " already exists");
            }
        }

        metrics.add(metric);
    }

    /**
     * Registers a string metric using the provided MetricSupplier.
     * The metric will be stored and managed by the FancyAnalyticsAPI instance.
     *
     * @param metric the MetricSupplier containing the name and value supplier of the string metric to be registered
     * @throws IllegalArgumentException if a metric with the same name already exists
     */
    public void registerStringMetric(MetricSupplier<String> metric) {
        for (MetricSupplier<?> m : metrics) {
            if (m.name().equals(metric.name())) {
                throw new IllegalArgumentException("Metric with name " + metric.name() + " already exists");
            }
        }

        metrics.add(metric);
    }

    /**
     * Registers a string array metric using the provided MetricSupplier.
     * The metric will be stored and managed by the FancyAnalyticsAPI instance.
     *
     * @param metric the MetricSupplier containing the name and value supplier of the string array metric to be registered
     * @throws IllegalArgumentException if a metric with the same name already exists
     */
    public void registerStringArrayMetric(MetricSupplier<String[]> metric) {
        for (MetricSupplier<?> m : metrics) {
            if (m.name().equals(metric.name())) {
                throw new IllegalArgumentException("Metric with name " + metric.name() + " already exists");
            }
        }

        metrics.add(metric);
    }
}
