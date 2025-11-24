package com.fancyinnovations.fancycore.api.permissions;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public interface PermissionStorage {

    @ApiStatus.Internal
    void storeGroup(Group group);

    @ApiStatus.Internal
    Group getGroup(String name);

    @ApiStatus.Internal
    void deleteGroup(String name);

    @ApiStatus.Internal
    List<Group> getAllGroups();

}
