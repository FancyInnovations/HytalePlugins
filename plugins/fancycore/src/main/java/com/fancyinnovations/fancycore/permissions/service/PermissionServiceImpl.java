package com.fancyinnovations.fancycore.permissions.service;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionService;
import com.fancyinnovations.fancycore.api.permissions.PermissionStorage;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.permissions.storage.fake.PermissionFakeStorage;

import java.util.List;
import java.util.UUID;

public class PermissionServiceImpl implements PermissionService {

    private static final PermissionStorage STORAGE = FancyCore.get().getPermissionStorage();

    private final PermissionStorage cache;

    public PermissionServiceImpl() {
        this.cache = new PermissionFakeStorage();
        load();
    }

    private void load() {
        for (Group g : STORAGE.getAllGroups()) {
            cache.storeGroup(g);
        }
    }

    @Override
    public List<Group> getGroups() {
        return cache.getAllGroups();
    }

    @Override
    public Group getGroup(String name) {
        return cache.getGroup(name);
    }

    @Override
    public void addGroup(Group group) {
        STORAGE.storeGroup(group);
        cache.storeGroup(group);
    }

    @Override
    public void removeGroup(String name) {
        STORAGE.deleteGroup(name);
        cache.deleteGroup(name);
    }

    @Override
    public boolean hasPermission(FancyPlayer player, String permission) {
        return false;
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        return false;
    }

    @Override
    public boolean hasPermission(String username, String permission) {
        return false;
    }
}
