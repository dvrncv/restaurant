package demo.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.Objects;

@Relation(collectionRelation = "ingredients", itemRelation = "ingredient")
public class IngredientResponse extends RepresentationModel<IngredientResponse> {
    private final Long id;
    private final String name;
    private final Integer quantity;
    private final LocalDate expirationDate;
    private final String unit;

    public IngredientResponse(Long id, String name, Integer quantity, LocalDate expirationDate, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IngredientResponse that = (IngredientResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(quantity, that.quantity) && Objects.equals(expirationDate, that.expirationDate) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, quantity, expirationDate, unit);
    }
}
