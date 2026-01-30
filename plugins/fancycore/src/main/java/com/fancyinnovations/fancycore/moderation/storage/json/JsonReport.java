package com.fancyinnovations.fancycore.moderation.storage.json;

import com.fancyinnovations.fancycore.api.moderation.PlayerReport;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.moderation.PlayerReportImpl;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public record JsonReport(
        String id,
        @SerializedName("reported_at") long reportedAt,
        @SerializedName("reported_player") String reportedPlayer,
        @SerializedName("reporting_player") String reportingPlayer,
        String reason,
        boolean resolved,
        @SerializedName("resolved_at") long resolvedAt
) {

    public static JsonReport from(PlayerReport report) {
        return new JsonReport(
                report.id(),
                report.reportedAt(),
                report.reportedPlayer().getData().getUUID().toString(),
                report.reportingPlayer().getData().getUUID().toString(),
                report.reason(),
                report.isResolved(),
                report.isResolved() ? report.resolvedAt() : -1
        );
    }

    public PlayerReport toPlayerReport() {
        FancyPlayer reportedFP = FancyPlayerService.get().getByUUID(UUID.fromString(reportedPlayer));
        FancyPlayer reportingFP = FancyPlayerService.get().getByUUID(UUID.fromString(reportingPlayer));

        return new PlayerReportImpl(
                id,
                reportedAt,
                reportedFP,
                reportingFP,
                reason,
                resolved,
                resolvedAt
        );
    }

}
