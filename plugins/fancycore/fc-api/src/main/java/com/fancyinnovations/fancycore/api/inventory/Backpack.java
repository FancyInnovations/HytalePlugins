package com.fancyinnovations.fancycore.api.inventory;

import java.util.UUID;

public record Backpack(
        UUID ownerUUID,
        String name,
        int size
) {

}
