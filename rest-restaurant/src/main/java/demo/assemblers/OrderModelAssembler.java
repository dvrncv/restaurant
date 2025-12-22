package demo.assemblers;


import demo.controllers.DishController;
import demo.controllers.OrderController;
import demo.dto.OrderResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<OrderResponse, EntityModel<OrderResponse>> {

    @Override
    public EntityModel<OrderResponse> toModel(OrderResponse order) {
        return EntityModel.of(
                order,
                linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders(null, 0, 10)).withRel("collection"),
                linkTo(methodOn(DishController.class).getAllDishes()).withRel("dishes")
        );
    }

    @Override
    public CollectionModel<EntityModel<OrderResponse>> toCollectionModel(Iterable<? extends OrderResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(OrderController.class).getAllOrders(null, 0, 10)).withSelfRel());
    }
}