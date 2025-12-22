package demo.endpoints;

import demo.dto.IngredientRequest;
import demo.dto.IngredientResponse;
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

@Tag(name = "ingredients", description = "API для работы с ингредиентами")
@RequestMapping("/api/ingredients")
public interface IngredientApi {

    @Operation(summary = "Получить все ингредиенты")
    @ApiResponse(responseCode = "200", description = "Список ингредиентов")
    @GetMapping
    CollectionModel<EntityModel<IngredientResponse>> getAlIngredients();

    @Operation(summary = "Получить ингредиент по ID")
    @ApiResponse(responseCode = "200", description = "Ингредиент найден")
    @ApiResponse(responseCode = "404", description = "Ингредиент не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<IngredientResponse> getIngredientById(@PathVariable Long id);

    @Operation(summary = "Создать новый ингредиент")
    @ApiResponse(responseCode = "201", description = "Ингредиент успешно создан")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<IngredientResponse>> createIngredients (@Valid @RequestBody IngredientRequest request);

    @Operation(summary = "Обновить ингредиент")
    @ApiResponse(responseCode = "200", description = "Ингредиент обновлен")
    @ApiResponse(responseCode = "404", description = "Ингредиент не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<IngredientResponse> updateIngredient(@PathVariable Long id, @Valid @RequestBody IngredientRequest request);

    @Operation(summary = "Удалить ингредиент")
    @ApiResponse(responseCode = "204", description = "Ингредиент удален")
    @ApiResponse(responseCode = "404", description = "Ингредиент не найден")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteIngredient(@PathVariable Long id);

}