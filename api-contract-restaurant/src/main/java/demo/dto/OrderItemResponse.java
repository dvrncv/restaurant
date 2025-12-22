package demo.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "orderItems", itemRelation = "orderItem")
public class OrderItemResponse extends RepresentationModel<OrderItemResponse> {
    private final Long dishId;
    private final String name;
    private final Integer quantity;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;

    public OrderItemResponse(Long dishId, String name, Integer quantity, LocalDateTime startedAt, LocalDateTime finishedAt) {
        this.dishId = dishId;
        this.name = name;
        this.quantity = quantity;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
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

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderItemResponse that = (OrderItemResponse) o;
        return Objects.equals(dishId, that.dishId) && Objects.equals(name, that.name) && Objects.equals(quantity, that.quantity) && Objects.equals(startedAt, that.startedAt) && Objects.equals(finishedAt, that.finishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dishId, name, quantity, startedAt, finishedAt);
    }
}