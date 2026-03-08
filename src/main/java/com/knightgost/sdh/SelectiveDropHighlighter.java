package com.knightgost.sdh;

import dev.faststats.core.ErrorTracker;
import dev.faststats.core.Metrics;
import dev.faststats.core.data.Metric;
import dev.faststats.fabric.FabricMetrics;
import net.fabricmc.api.ModInitializer;

public class SelectiveDropHighlighter implements ModInitializer {

    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    private Metrics metrics;

    @Override
    public void onInitialize() {
        // Load the config first so the metrics have data to read
        SDHConfig.load();

        // Faststats their
        this.metrics = FabricMetrics.factory()
                .errorTracker(ERROR_TRACKER)
                .debug(false)

                .token("cda95c1370280705c7f6935d86236fb3")

                // FIX: Access HIGHLIGHTED_ITEMS directly because it is static
                .addMetric(Metric.number("highlighted_items_count", () ->
                        SDHConfig.HIGHLIGHTED_ITEMS.size()
                ))

                .create("selective-drop-highlighter");

        metrics.ready();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (metrics != null) metrics.shutdown();
        }));
    }
}