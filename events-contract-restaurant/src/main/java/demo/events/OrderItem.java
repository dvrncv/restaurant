package demo.events;

import java.io.Serializable;

public record OrderItem(
        Long dishId,
        String dishName,
        Integer quantity
) implements Serializable {}
