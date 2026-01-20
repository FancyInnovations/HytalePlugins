package com.fancyinnovations.fancycore.permissions.storage.json;

import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.permissions.PermissionStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.File;
import java.util.List;

public class PermissionJsonStorage implements PermissionStorage {

    private static final String DATA_DIR_PATH = com.fancyinnovations.fancycore.config.FancyCorePaths.GROUPS_DATA_DIR;
    private final JDB db;

    public PermissionJsonStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    @Override
    public void storeGroup(Group group) {
        try {
            db.set(group.getName(), JsonGroup.from(group));
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
            JsonGroup jsonGroup = db.get(name, JsonGroup.class);
            if (jsonGroup == null) {
                return null;
            }
            return jsonGroup.toGroup();
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
            return db.getAll("", JsonGroup.class).stream()
                    .map(JsonGroup::toGroup)
                    .toList();
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load all Groups",
                    ThrowableProperty.of(e)
            );
        }

        return List.of();
    }

    @Override
    public int countGroups() {
        File dir = new File(DATA_DIR_PATH);
        File[] groupFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (groupFiles == null) {
            return 0;
        }
        return groupFiles.length;
    }
}
