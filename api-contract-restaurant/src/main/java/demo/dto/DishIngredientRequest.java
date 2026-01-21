package demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DishIngredientRequest(
        @NotNull(message = "ID ингредиента не может быть null")
        Long ingredientId,
        @Positive(message = "Количество ингредиента должно быть положительным")
        Integer quantity
) {}

