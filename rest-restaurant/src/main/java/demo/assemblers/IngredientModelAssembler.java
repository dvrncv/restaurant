package demo.assemblers;


import demo.controllers.IngredientController;
import demo.dto.IngredientResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class IngredientModelAssembler implements RepresentationModelAssembler<IngredientResponse, EntityModel<IngredientResponse>> {

    @Override
    public EntityModel<IngredientResponse> toModel(IngredientResponse ingredient) {
        return EntityModel.of(
                ingredient,
                linkTo(methodOn(IngredientController.class).getIngredientById(ingredient.getId())).withSelfRel(),
                linkTo(methodOn(IngredientController.class).getAlIngredients()).withRel("collection")
        );
    }

    @Override
    public CollectionModel<EntityModel<IngredientResponse>> toCollectionModel(Iterable<? extends IngredientResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(IngredientController.class).getAlIngredients()).withSelfRel());
    }
}
