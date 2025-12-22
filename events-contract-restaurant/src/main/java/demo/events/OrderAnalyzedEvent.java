package demo.events;

import java.io.Serializable;

public record OrderAnalyzedEvent(
        Long orderId,
        Integer totalItems,
        Integer complexityScore,
        String verdict
) implements Serializable {}