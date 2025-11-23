package com.fancyinnovations.fancycore.api.events.service;

import com.fancyinnovations.fancycore.api.events.FancyEvent;

public interface EventListener<T extends FancyEvent> {

    void on(T event);

}
