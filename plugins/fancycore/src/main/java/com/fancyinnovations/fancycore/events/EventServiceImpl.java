package com.fancyinnovations.fancycore.events;

import com.fancyinnovations.fancycore.api.events.FancyEvent;
import com.fancyinnovations.fancycore.api.events.service.EventListener;
import com.fancyinnovations.fancycore.api.events.service.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventServiceImpl implements EventService {

    private final Map<Class<? extends FancyEvent>, List<EventListener<? extends FancyEvent>>> listeners;

    public EventServiceImpl() {
        this.listeners = new ConcurrentHashMap<>();
    }

    @Override
    public boolean fireEvent(FancyEvent event) {
        if (!this.listeners.containsKey(event.getClass())) {
            return true;
        }

        for (EventListener<? extends FancyEvent> listener : this.listeners.get(event.getClass())) {
            ((EventListener<FancyEvent>) listener).on(event);

            if (event.isCancellable() && event.isCancelled()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void registerListener(Class<? extends FancyEvent> event, EventListener<?> listener) {
        if (this.listeners.containsKey(event)) {
            this.listeners.get(event).add(listener);
        } else {
            List<EventListener<? extends FancyEvent>> listeners = new ArrayList<>();
            this.listeners.put(event, listeners);
        }
    }
}
