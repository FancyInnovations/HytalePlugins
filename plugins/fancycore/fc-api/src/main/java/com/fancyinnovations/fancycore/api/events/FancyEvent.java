package com.fancyinnovations.fancycore.api.events;

public abstract class FancyEvent {

    private final long firedAt;
    private final boolean isCancellable;
    private boolean cancelled;

    public FancyEvent() {
        this.firedAt = System.currentTimeMillis();
        this.isCancellable = true;
        this.cancelled = false;
    }

    public FancyEvent(boolean isCancellable) {
        this.firedAt = System.currentTimeMillis();
        this.isCancellable = isCancellable;
        this.cancelled = false;
    }

    public void fire()  {
        // TODO: Implement event firing logic
    }

    public boolean isCancellable() {
        return isCancellable;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public long firedAt() {
        return firedAt;
    }
}
