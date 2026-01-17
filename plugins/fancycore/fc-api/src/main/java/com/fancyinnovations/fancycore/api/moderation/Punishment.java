package com.fancyinnovations.fancycore.api.moderation;

import java.util.UUID;

public interface Punishment {

    String id();

    UUID player();

    PunishmentType type();

    String reason();

    long issuedAt();

    UUID issuedBy();

    long expiresAt();

    long remainingDuration();

    boolean isActive();
}
