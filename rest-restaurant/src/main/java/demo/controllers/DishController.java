package demo.controllers;


import demo.assemblers.DishModelAssembler;
import demo.dto.DishRequest;
import demo.dto.DishResponse;
import demo.endpoints.DishApi;
import demo.services.DishService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DishController implements DishApi {
    private final DishService dishService;
    private final DishModelAssembler dishModelAssembler;

    public DishController(DishService dishService, DishModelAssembler dishModelAssembler) {
        this.dishService = dishService;
        this.dishModelAssembler = dishModelAssembler;
    }

    @Override
    public CollectionModel<EntityModel<DishResponse>> getAllDishes() {
        List<DishResponse> dishes = dishService.findAllDishes();
        return dishModelAssembler.toCollectionModel(dishes);
    }

    @Override
    public EntityModel<DishResponse> getDishById(Long id){
        DishResponse dish = dishService.findDishById(id);
        return dishModelAssembler.toModel(dish);
    }

    @Override
    public ResponseEntity<EntityModel<DishResponse>> createDish (DishRequest request){
        DishResponse createdDish = dishService.createDish(request);
        EntityModel<DishResponse> entityModel = dishModelAssembler.toModel(createdDish);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<DishResponse> updateDish(Long id, DishRequest request){
        DishResponse updateDish = dishService.updateDish(id, request);
        return dishModelAssembler.toModel(updateDish);
    }

    @Override
    public void deleteDish(Long id){
        dishService.deleteDish(id);
    }
}
