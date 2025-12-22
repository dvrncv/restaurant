package demo.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record IngredientRequest(
        @NotBlank(message = "Название ингредиента не может быть пустым")
        String name,
        @Positive(message = "Количество должно быть положительным")
        Integer quantity,
        @FutureOrPresent(message = "Срок годности должен быть в будущем или настоящем")
        LocalDate expirationDate,
        @NotBlank(message = "Единицы должны быть указаны")
        String unit
) {
}