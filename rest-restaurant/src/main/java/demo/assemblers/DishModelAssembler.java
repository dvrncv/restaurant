package demo.assemblers;

import demo.controllers.DishController;
import demo.controllers.IngredientController;
import demo.dto.DishResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DishModelAssembler implements RepresentationModelAssembler<DishResponse, EntityModel<DishResponse>> {

    @Override
    public EntityModel<DishResponse> toModel(DishResponse dish) {
        return EntityModel.of(
                dish,
                linkTo(methodOn(DishController.class).getDishById(dish.getId())).withSelfRel(),
                linkTo(methodOn(IngredientController.class).getAlIngredients()).withRel("ingredients"),
                linkTo(methodOn(DishController.class).getAllDishes()).withRel("collection")
        );
    }

    @Override
    public CollectionModel<EntityModel<DishResponse>> toCollectionModel(Iterable<? extends DishResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(DishController.class).getAllDishes()).withSelfRel());
    }
}