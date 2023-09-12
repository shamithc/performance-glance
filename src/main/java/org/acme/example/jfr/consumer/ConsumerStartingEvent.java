package org.acme.example.jfr.consumer;

import org.springframework.context.ApplicationEvent;

public class ConsumerStartingEvent extends ApplicationEvent {

    public ConsumerStartingEvent(Object source) {
        super(source);
    }

    // You can add custom methods or fields here if needed
}
