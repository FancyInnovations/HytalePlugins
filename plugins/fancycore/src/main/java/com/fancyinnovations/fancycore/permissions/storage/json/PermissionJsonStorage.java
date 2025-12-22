package com.fancyinnovations.fancycore.permissions.storage.json;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.util.List;

public class PermissionJsonStorage implements PermissionStorage {

    private static final String DATA_DIR_PATH = "plugins/FancyCore/data/groups";
    private final JDB db;

    public PermissionJsonStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    @Override
    public void storeGroup(Group group) {
        try {
            db.set(group.getName(), group);
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Group",
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public Group getGroup(String name) {
        try {
            return db.get(name, Group.class);
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load Group",
                    ThrowableProperty.of(e)
            );
        }

        return null;
    }

    @Override
    public void deleteGroup(String name) {
        db.delete(name);
    }

    @Override
    public List<Group> getAllGroups() {
        try {
            return db.getAll("", Group.class);
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load all Groups",
                    ThrowableProperty.of(e)
            );
        }

        return List.of();
    }
}
