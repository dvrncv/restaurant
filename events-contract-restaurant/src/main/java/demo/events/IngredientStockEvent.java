package demo.events;

import java.io.Serializable;

public record IngredientStockEvent(
        Long ingredientId,
        String ingredientName,
        Integer currentQuantity,
        Integer criticalThreshold
) implements Serializable {}

