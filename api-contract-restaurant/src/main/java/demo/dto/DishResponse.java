package demo.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "dishes", itemRelation = "dish")
public class DishResponse extends RepresentationModel<DishResponse> {
    private final Long id;
    private final String name;
    private List<IngredientResponse> ingredients;

    public DishResponse(Long id, String name, List<IngredientResponse> ingredients) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<IngredientResponse> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DishResponse that = (DishResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(ingredients, that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, ingredients);
    }
}