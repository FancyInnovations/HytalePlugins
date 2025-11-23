package com.fancyinnovations.fancycore.api.events.service;

import com.fancyinnovations.fancycore.api.events.FancyEvent;

public interface EventService {

    /**
     * Fires an event to all registered listeners.
     *
     * @param event the event to fire
     * @return true if the event was not cancelled, false otherwise
     */
    boolean fireEvent(FancyEvent event);

    /**
     * Registers a listener for a specific event type.
     *
     * @param event    the event class to listen for
     * @param listener the listener to register
     */
    void registerListener(Class<? extends FancyEvent> event, EventListener<?> listener);

}
