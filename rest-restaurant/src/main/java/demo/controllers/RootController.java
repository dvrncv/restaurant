package demo.controllers;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootController {
    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();
        rootModel.add(
                linkTo(methodOn(DishController.class).getAllDishes()).withRel("dishes"),
                linkTo(methodOn(OrderController.class).getAllOrders(null, 0, 10)).withRel("orders"),
                linkTo(methodOn(IngredientController.class).getAlIngredients()).withRel("ingredients")

        );
        rootModel.add(Link.of("/swagger-ui.html", "documentation"));
        return rootModel;
    }
}
