package demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull(message = "Id блюда обязательно")
        Long dishId,
        @Positive(message = "Количество должно быть положительным числом")
        Integer quantity
) {
}

