package demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

import java.util.List;

public record DishRequest(
        @NotBlank(message = "Название блюда не может быть пустым")
        String name,
        @NotEmpty(message = "Список ингредиентов не может быть пустым")
        @Valid
        List<DishIngredientRequest> ingredients
) {}
