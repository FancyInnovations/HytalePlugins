package com.fancyinnovations.fancycore.moderation;

import com.fancyinnovations.fancycore.api.moderation.Punishment;
import com.fancyinnovations.fancycore.api.moderation.PunishmentType;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public record PunishmentImpl(
        String id,
        UUID player,
        PunishmentType type,
        String reason,
        @SerializedName("issued_at") long issuedAt,
        @SerializedName("issued_by") UUID issuedBy,
        @SerializedName("expires_at") long expiresAt
) implements Punishment {

    public long remainingDuration() {
        if (expiresAt <= 0) {
            return -1; // Permanent punishment
        }
        return expiresAt - System.currentTimeMillis();
    }

    public boolean isActive() {
        if (type == PunishmentType.KICK || type == PunishmentType.WARNING) {
            return false;
        }

        return expiresAt <= 0 || System.currentTimeMillis() < expiresAt;
    }

}
