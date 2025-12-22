package demo.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "orders", itemRelation = "order")
public class OrderResponse extends RepresentationModel<OrderResponse> {
    private final Long id;
    private final List<OrderItemResponse> dishes;
    private final String status;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public OrderResponse(Long id, List<OrderItemResponse> dishes, String status,
                         LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.dishes = dishes;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public List<OrderItemResponse> getDishes() {
        return dishes;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderResponse)) return false;
        if (!super.equals(o)) return false;
        OrderResponse that = (OrderResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(dishes, that.dishes) &&
                Objects.equals(status, that.status) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, dishes, status, startTime, endTime);
    }
}