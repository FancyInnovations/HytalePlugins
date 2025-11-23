package com.fancyinnovations.fancycore.punishments;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.punishments.PlayerReport;

import java.util.UUID;

public class PlayerReportImpl implements PlayerReport {

    private final UUID id;
    private final long reportedAt;
    private final FancyPlayer reportedPlayer;
    private final FancyPlayer reportingPlayer;
    private final String reason;
    private boolean resolved;
    private long resolvedAt;

    public PlayerReportImpl(UUID id, FancyPlayer reportedPlayer, FancyPlayer reportingPlayer, String reason) {
        this.id = id;
        this.reportedAt = System.currentTimeMillis();
        this.reportedPlayer = reportedPlayer;
        this.reportingPlayer = reportingPlayer;
        this.reason = reason;
        this.resolved = false;
        this.resolvedAt = -1;
    }

    @Override
    public UUID id() {
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

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public long resolvedAt() {
        return resolvedAt;
    }
}
