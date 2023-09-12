package org.acme.example.jfr.consumer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerStartingEventListener{

    @EventListener
    public void handleConsumerStartingEvent(ConsumerStartingEvent event) {
        // Handle the event here
        System.out.println("Consumer is starting!");
        // ... your custom logic
    }
}

