package demo.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import demo.dto.IngredientRequest;
import demo.dto.IngredientResponse;
import demo.services.IngredientService;

import java.util.List;
import java.util.Map;

@DgsComponent
public class IngredientDataFetcher {

    private final IngredientService ingredientService;

    public IngredientDataFetcher(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @DgsQuery
    public List<IngredientResponse> ingredients() {
        return ingredientService.findAllIngredients();
    }

    @DgsQuery
    public IngredientResponse ingredientById(@InputArgument Long id) {
        return ingredientService.findIngredientById(id);
    }

    @DgsMutation
    public IngredientResponse createIngredient(@InputArgument("input") Map<String, Object> input) {
        IngredientRequest request = new IngredientRequest(
                (String) input.get("name"),
                (Integer) input.get("quantity"),
                java.time.LocalDate.parse((String) input.get("expirationDate")),
                (String) input.get("unit")
        );
        return ingredientService.createIngredient(request);
    }

    @DgsMutation
    public IngredientResponse updateIngredient(
            @InputArgument("id") Long id,
            @InputArgument("input") Map<String, Object> input
    ) {
        IngredientRequest request = new IngredientRequest(
                (String) input.get("name"),
                (Integer) input.get("quantity"),
                java.time.LocalDate.parse((String) input.get("expirationDate")),
                (String) input.get("unit")
        );
        return ingredientService.updateIngredient(id, request);
    }

    @DgsMutation
    public Long deleteIngredient(@InputArgument Long id) {
        ingredientService.deleteIngredient(id);
        return id;
    }
}
