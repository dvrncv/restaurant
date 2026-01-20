package demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;


@Relation(collectionRelation = "orderItems", itemRelation = "orderItem")
public class OrderItemResponse extends RepresentationModel<OrderItemResponse> {
    private Long dishId;
    private String name;
    private Integer quantity;
    private LocalDateTime startedAt;


    public OrderItemResponse(Long dishId, String name, Integer quantity, LocalDateTime startedAt) {
        this.dishId = dishId;
        this.name = name;
        this.quantity = quantity;
        this.startedAt = startedAt;
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

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    public LocalDateTime getStartedAt() {
        return startedAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderItemResponse that = (OrderItemResponse) o;
        return Objects.equals(dishId, that.dishId) && Objects.equals(name, that.name) && Objects.equals(quantity, that.quantity) && Objects.equals(startedAt, that.startedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dishId, name, quantity, startedAt);
    }
}