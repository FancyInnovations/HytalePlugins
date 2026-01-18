package com.fancyinnovations.fancycore.permissions.storage.fake;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionStorage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionFakeStorage implements PermissionStorage {

    private final Map<String, Group> groups;

    public PermissionFakeStorage() {
        this.groups = new ConcurrentHashMap<>();
    }

    @Override
    public void storeGroup(Group group) {
        groups.put(group.getName(), group);
    }

    @Override
    public Group getGroup(String name) {
        return groups.get(name);
    }

    @Override
    public void deleteGroup(String name) {
        groups.remove(name);
    }

    @Override
    public List<Group> getAllGroups() {
        return List.copyOf(groups.values());
    }

    @Override
    public int countGroups() {
        return groups.size();
    }
}
