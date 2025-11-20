package com.fancyinnovations.fancycore.player;

import java.awt.*;
import java.util.UUID;

public class FancyPlayerImpl {

    private final UUID uuid;
    private final String username;
    private String nickname;
    private Color chatColor;
    private double balance;
    private long firstLoginTime; // timestamp
    private long playTime; // in milliseconds

    private boolean isDirty;

    /**
     * Constructor for a new FancyPlayer
     */
    public FancyPlayerImpl(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.nickname = username; // default nickname is the username
        this.chatColor = Color.WHITE;
        this.balance = 0.0;
        this.firstLoginTime = System.currentTimeMillis();
        this.playTime = 0L;
        this.isDirty = true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.isDirty = true;
    }

    public Color getChatColor() {
        return chatColor;
    }

    public void setChatColor(Color chatColor) {
        this.chatColor = chatColor;
        this.isDirty = true;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        this.isDirty = true;
    }

    public void addBalance(double amount) {
        this.balance += amount;
        this.isDirty = true;
    }

    public void removeBalance(double amount) {
        this.balance -= amount;
        this.isDirty = true;
    }

    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    public void setFirstLoginTime(long firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
        this.isDirty = true;
    }

    public void addPlayTime(long additionalTime) {
        this.playTime += additionalTime;
        this.isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
