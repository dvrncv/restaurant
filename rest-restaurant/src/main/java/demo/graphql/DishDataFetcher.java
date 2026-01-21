package demo.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import demo.dto.DishIngredientRequest;
import demo.dto.DishRequest;
import demo.dto.DishResponse;
import demo.services.DishService;

import java.util.List;
import java.util.Map;

@DgsComponent
public class DishDataFetcher {

    private final DishService dishService;

    public DishDataFetcher(DishService dishService) {
        this.dishService = dishService;
    }

    @DgsQuery
    public List<DishResponse> dishes() {
        return dishService.findAllDishes();
    }

    @DgsQuery
    public DishResponse dishById(@InputArgument Long id) {
        return dishService.findDishById(id);
    }

    @DgsMutation
    public DishResponse createDish(@InputArgument("input") Map<String, Object> input) {
        List<DishIngredientRequest> dishIngredients = ((List<Map<String, Object>>) input.get("ingredients"))
                .stream()
                .map(ingredientMap -> new DishIngredientRequest(
                        Long.parseLong(ingredientMap.get("ingredientId").toString()),
                        (Integer) ingredientMap.get("quantity")
                ))
                .toList();

        DishRequest request = new DishRequest(
                (String) input.get("name"),
                (Integer) input.get("durationTime"),
                dishIngredients
        );
        return dishService.createDish(request);
    }

    @DgsMutation
    public DishResponse updateDish(
            @InputArgument("id") Long id,
            @InputArgument("input") Map<String, Object> input
    ) {
        List<DishIngredientRequest> dishIngredients = ((List<Map<String, Object>>) input.get("ingredients"))
                .stream()
                .map(ingredientMap -> new DishIngredientRequest(
                        Long.parseLong(ingredientMap.get("ingredientId").toString()),
                        (Integer) ingredientMap.get("quantity")
                ))
                .toList();

        DishRequest request = new DishRequest(
                (String) input.get("name"),
                (Integer) input.get("durationTime"),
                dishIngredients
        );
        return dishService.updateDish(id, request);
    }

    @DgsMutation
    public Long deleteDish(@InputArgument Long id) {
        dishService.deleteDish(id);
        return id;
    }
}