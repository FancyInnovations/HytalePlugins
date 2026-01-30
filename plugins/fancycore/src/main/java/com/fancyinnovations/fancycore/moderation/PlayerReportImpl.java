package com.fancyinnovations.fancycore.moderation;

import com.fancyinnovations.fancycore.api.moderation.PlayerReport;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;

public class PlayerReportImpl implements PlayerReport {

    private final String id;
    private final long reportedAt;
    private final FancyPlayer reportedPlayer;
    private final FancyPlayer reportingPlayer;
    private final String reason;
    private boolean resolved;
    private long resolvedAt;

    public PlayerReportImpl(String id, FancyPlayer reportedPlayer, FancyPlayer reportingPlayer, String reason) {
        this.id = id;
        this.reportedAt = System.currentTimeMillis();
        this.reportedPlayer = reportedPlayer;
        this.reportingPlayer = reportingPlayer;
        this.reason = reason;
        this.resolved = false;
        this.resolvedAt = -1;
    }

    public PlayerReportImpl(String id, long reportedAt, FancyPlayer reportedPlayer, FancyPlayer reportingPlayer, String reason, boolean resolved, long resolvedAt) {
        this.id = id;
        this.reportedAt = reportedAt;
        this.reportedPlayer = reportedPlayer;
        this.reportingPlayer = reportingPlayer;
        this.reason = reason;
        this.resolved = resolved;
        this.resolvedAt = resolvedAt;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public long reportedAt() {
        return reportedAt;
    }

    @Override
    public FancyPlayer reportedPlayer() {
        return reportedPlayer;
    }

    @Override
    public FancyPlayer reportingPlayer() {
        return reportingPlayer;
    }

    @Override
    public String reason() {
        return reason;
    }

    @Override
    public boolean isResolved() {
        return resolved;
    }

    public void resolve() {
        this.resolved = true;
        this.resolvedAt = System.currentTimeMillis();
    }

    public long resolvedAt() {
        return resolvedAt;
    }
}
