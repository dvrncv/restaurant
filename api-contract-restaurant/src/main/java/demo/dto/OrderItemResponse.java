package demo.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "orderItems", itemRelation = "orderItem")
public class OrderItemResponse extends RepresentationModel<OrderItemResponse> {
    private final Long dishId;
    private final String name;
    private final Integer quantity;

    public OrderItemResponse(Long dishId, String name, Integer quantity) {
        this.dishId = dishId;
        this.name = name;
        this.quantity = quantity;
    }

    public Long getDishId() {
        return dishId;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderItemResponse that = (OrderItemResponse) o;
        return Objects.equals(dishId, that.dishId) && Objects.equals(name, that.name) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dishId, name, quantity);
    }
}