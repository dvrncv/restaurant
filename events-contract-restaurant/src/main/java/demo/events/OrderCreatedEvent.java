package demo.events;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        List<OrderItem> items,
        LocalDateTime startTime,
        String status
) implements Serializable {}

