package demo.endpoints;

import demo.dto.OrderRequest;
import demo.dto.OrderResponse;
import demo.dto.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "orders", description = "API для работы с заказами")
@RequestMapping("/api/orders")
public interface OrderApi {

    @Operation(summary = "Получить список всех заказов с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Список заказов")
    @GetMapping
    PagedModel<EntityModel<OrderResponse>> getAllOrders(
            @Parameter(description = "Фильтр по ID заказа") @RequestParam(required = false) Long orderId,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "Получить заказ по ID")
    @ApiResponse(responseCode = "200", description = "Заказ найден")
    @ApiResponse(responseCode = "404", description = "Заказ не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<OrderResponse> getOrderById (@PathVariable("id") Long id);

    @Operation(summary = "Создать новый заказ")
    @ApiResponse(responseCode = "201", description = "Заказ успешно создан")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<OrderResponse>> createOrder (@Valid @RequestBody OrderRequest request);

    @Operation(summary = "Обновить заказ")
    @ApiResponse(responseCode = "200", description = "Заказ обновлен")
    @ApiResponse(responseCode = "404", description = "Заказ не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<OrderResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest request);

    @Operation(summary = "Обновить статус заказа")
    @ApiResponse(responseCode = "200", description = "Статус заказа обновлен")
    @ApiResponse(responseCode = "404", description = "Заказ не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PatchMapping("/{id}/status")
    EntityModel<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam String status);

    @Operation(summary = "Удалить заказ")
    @ApiResponse(responseCode = "204", description = "Заказ удален")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteOrder(@PathVariable Long id);
}