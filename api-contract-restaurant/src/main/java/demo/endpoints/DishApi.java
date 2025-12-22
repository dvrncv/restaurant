package demo.endpoints;

import demo.dto.DishRequest;
import demo.dto.DishResponse;
import demo.dto.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "dishes", description = "API для работы с блюдами")
@RequestMapping("/api/dishes")
public interface DishApi {

    @Operation(summary = "Получить все блюда")
    @ApiResponse(responseCode = "200", description = "Список блюд")
    @GetMapping
    CollectionModel<EntityModel<DishResponse>> getAllDishes();

    @Operation(summary = "Получить блюдо по ID")
    @ApiResponse(responseCode = "200", description = "Блюдо найдено")
    @ApiResponse(responseCode = "404", description = "Блюдо не найдено", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<DishResponse> getDishById(@PathVariable Long id);

    @Operation(summary = "Создать новое блюдо")
    @ApiResponse(responseCode = "201", description = "Блюдо успешно создано")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<DishResponse>> createDish (@Valid @RequestBody DishRequest request);

    @Operation(summary = "Обновить блюдо")
    @ApiResponse(responseCode = "200", description = "Блюдо обновлено")
    @ApiResponse(responseCode = "404", description = "Блюдо не найдено", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<DishResponse> updateDish(@PathVariable Long id, @Valid @RequestBody DishRequest request);

    @Operation(summary = "Удалить блюдо")
    @ApiResponse(responseCode = "204", description = "Блюдо удалено")
    @ApiResponse(responseCode = "404", description = "Блюдо не найдено")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDish(@PathVariable Long id);
}
