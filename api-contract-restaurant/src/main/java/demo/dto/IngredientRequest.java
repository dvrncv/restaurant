package demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record IngredientRequest(
        @NotBlank(message = "Название ингредиента не может быть пустым")
        String name,
        @Positive(message = "Количество должно быть положительным")
        Integer quantity,
        @NotBlank(message = "Единицы должны быть указаны")
        String unit
) {
}