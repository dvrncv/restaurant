package demo.controllers;

import demo.assemblers.IngredientModelAssembler;
import demo.dto.IngredientRequest;
import demo.dto.IngredientResponse;
import demo.endpoints.IngredientApi;
import demo.services.IngredientService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IngredientController implements IngredientApi {

    private final IngredientService ingredientService;
    private final IngredientModelAssembler ingredientModelAssembler;

    public IngredientController(IngredientService ingredientService, IngredientModelAssembler ingredientModelAssembler) {
        this.ingredientService = ingredientService;
        this.ingredientModelAssembler = ingredientModelAssembler;
    }

    @Override
    public CollectionModel<EntityModel<IngredientResponse>> getAlIngredients(){
        List<IngredientResponse> ingredients = ingredientService.findAllIngredients();
        return ingredientModelAssembler.toCollectionModel(ingredients);
    }

    @Override
    public EntityModel<IngredientResponse> getIngredientById(Long id){
        IngredientResponse ingredient = ingredientService.findIngredientById(id);
        return ingredientModelAssembler.toModel(ingredient);
    }


    @Override
    public ResponseEntity<EntityModel<IngredientResponse>> createIngredients (IngredientRequest request){
        IngredientResponse createIngredient = ingredientService.createIngredient(request);
        EntityModel<IngredientResponse> entityModel = ingredientModelAssembler.toModel(createIngredient);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<IngredientResponse> updateIngredient(Long id, IngredientRequest request){
        IngredientResponse updateIngredient = ingredientService.updateIngredient(id, request);
        return ingredientModelAssembler.toModel(updateIngredient);
    }

    @Override
    public void deleteIngredient(Long id){
        ingredientService.deleteIngredient(id);
    }
}