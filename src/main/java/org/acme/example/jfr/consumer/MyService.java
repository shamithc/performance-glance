package org.acme.example.jfr.consumer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private final ApplicationEventPublisher eventPublisher;

    public MyService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void someMethod() {
        // ... your business logic

        // Publish the custom event
        eventPublisher.publishEvent(new ConsumerStartingEvent(this));
    }
}

