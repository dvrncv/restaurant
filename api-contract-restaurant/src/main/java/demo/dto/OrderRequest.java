package demo.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotEmpty(message = "Список блюд не может быть пустым")
        List<OrderItemRequest> dishes
) {
}
