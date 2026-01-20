package com.fancyinnovations.fancycore.api.events;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.discord.Embed;
import com.fancyinnovations.fancycore.api.discord.Message;
import com.fancyinnovations.fancycore.api.translations.MessageKey;

import java.util.List;

/**
 * The base class for all events in the FancyCore system.
 */
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

    /**
     * Fires this event using the FancyCore EventService.
     *
     * @return true if the event was not cancelled, false otherwise
     */
    public final boolean fire() {
        return FancyCore.get().getEventService().fireEvent(this);
    }

    /**
     * Checks if this event is cancellable.
     *
     * @return true if the event is cancellable, false otherwise
     */
    public final boolean isCancellable() {
        return isCancellable;
    }

    /**
     * Checks if this event has been cancelled.
     *
     * @return true if the event is cancelled, false otherwise
     */
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancels this event if it is cancellable.
     */
    public final void cancel() {
        if (!isCancellable) {
            throw new UnsupportedOperationException("This event cannot be cancelled.");
        }

        this.cancelled = true;
    }

    /**
     * Gets the timestamp when this event was fired.
     *
     * @return the timestamp in milliseconds
     */
    public final long firedAt() {
        return firedAt;
    }

    /**
     * Gets a Discord message representation of this event.
     *
     * @return a Message object for Discord, or null if not applicable
     */
    public Message getDiscordMessage() {
        String message = FancyCore.get().getTranslationService()
                .getMessage(MessageKey.EVENT_GENERIC)
                .replace("event_type", this.getClass().getSimpleName())
                .getParsedMessage();

        String embedTitle = FancyCore.get().getTranslationService().getRaw(MessageKey.DISCORD_EVENT_FIRED);

        return new Message(
                message,
                List.of(
                        new Embed(
                                embedTitle,
                                "Event Type: " + this.getClass().getSimpleName() + "\nFired At: <t:"+firedAt+":f>",
                                0x3498db
                        )
                )
        );
    }
}
