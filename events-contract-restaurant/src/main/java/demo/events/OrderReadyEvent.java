package demo.events;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record OrderReadyEvent(
        Long orderId,
        List<OrderItem> items,
        LocalDateTime readyTime,
        String status
) implements Serializable {}
