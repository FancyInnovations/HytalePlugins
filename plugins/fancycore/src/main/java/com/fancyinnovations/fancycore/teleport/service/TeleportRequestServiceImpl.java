package com.fancyinnovations.fancycore.teleport.service;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.teleport.TeleportRequestService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportRequestServiceImpl implements TeleportRequestService {

    // Map: target UUID -> Set of sender UUIDs who have pending requests
    private final Map<UUID, Set<UUID>> requests;

    public TeleportRequestServiceImpl() {
        this.requests = new ConcurrentHashMap<>();
    }

    @Override
    public boolean sendRequest(FancyPlayer sender, FancyPlayer target) {
        UUID senderUUID = sender.getData().getUUID();
        UUID targetUUID = target.getData().getUUID();

        // Check if request already exists
        Set<UUID> targetRequests = requests.get(targetUUID);
        if (targetRequests != null && targetRequests.contains(senderUUID)) {
            return false;
        }

        // Add request
        requests.computeIfAbsent(targetUUID, k -> ConcurrentHashMap.newKeySet()).add(senderUUID);
        return true;
    }

    @Override
    public UUID getRequest(FancyPlayer target, FancyPlayer sender) {
        UUID targetUUID = target.getData().getUUID();
        UUID senderUUID = sender.getData().getUUID();

        Set<UUID> targetRequests = requests.get(targetUUID);
        if (targetRequests != null && targetRequests.contains(senderUUID)) {
            return senderUUID;
        }
        return null;
    }

    @Override
    public UUID getFirstRequest(FancyPlayer target) {
        UUID targetUUID = target.getData().getUUID();
        Set<UUID> targetRequests = requests.get(targetUUID);
        if (targetRequests != null && !targetRequests.isEmpty()) {
            return targetRequests.iterator().next();
        }
        return null;
    }

    @Override
    public boolean removeRequest(FancyPlayer target, FancyPlayer sender) {
        UUID targetUUID = target.getData().getUUID();
        UUID senderUUID = sender.getData().getUUID();

        Set<UUID> targetRequests = requests.get(targetUUID);
        if (targetRequests != null && targetRequests.remove(senderUUID)) {
            if (targetRequests.isEmpty()) {
                requests.remove(targetUUID);
            }
            return true;
        }
        return false;
    }

    @Override
    public void removeAllRequests(FancyPlayer target) {
        UUID targetUUID = target.getData().getUUID();
        requests.remove(targetUUID);
    }
}
