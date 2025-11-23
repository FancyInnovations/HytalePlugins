package com.fancyinnovations.fancycore.api.permissions;

public interface Permission {

    String getPermission();

    boolean isEnabled();
    void setEnabled(boolean enabled);

}
