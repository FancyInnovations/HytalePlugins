package com.fancyinnovations.fancycore.api.events.player;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;

/**
 * Event fired when a player's data is modified.
 * Only fires for modifications made through FancyPlayer's setters.
 */
public class PlayerModifiedEvent extends PlayerEvent {

    private final ModifiedField modifiedField;
    private final Object oldData;
    private  Object newData;

    public PlayerModifiedEvent(FancyPlayer player, ModifiedField modifiedField, Object oldData, Object newData) {
        super(player);
        this.modifiedField = modifiedField;
        this.oldData = oldData;
        this.newData = newData;
    }

    /**
     * Gets the field that was modified.
     * @return the modified field.
     */
    public ModifiedField getModifiedField() {
        return modifiedField;
    }

    /**
     * Gets the old data before modification.
     * @return the old data.
     */
    public Object getOldData() {
        return oldData;
    }

    /**
     * Gets the new data after modification.
     * @return the new data.
     */
    public Object getNewData() {
        return newData;
    }

    /**
     * Sets the new data after modification.
     * @param newData the new data.
     */
    public void setNewData(FancyPlayer newData) {
        this.newData = newData;
    }

    public enum ModifiedField {
        PERMISSIONS,
        GROUPS,
        NICKNAME,
        CHAT_COLOR,
        BALANCE,
        FIRST_LOGIN_TIME,
        PLAY_TIME
    }
}
