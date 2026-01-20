package demo.events;

import java.io.Serializable;
import java.util.List;

public record OrderAnalyzedEvent(
        Long orderId,
        String orderStartTime,
        String orderReadyTime,
        Integer dishCount,
        Integer totalItems,
        Integer complexityScore,
        Integer averageTimePerDish,
        List<String> recommendations
) implements Serializable {}

