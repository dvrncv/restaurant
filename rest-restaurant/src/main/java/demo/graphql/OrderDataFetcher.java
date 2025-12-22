package demo.graphql;

import com.netflix.graphql.dgs.*;
import demo.dto.*;
import demo.services.OrderService;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.Map;

@DgsComponent
public class OrderDataFetcher {

    private final OrderService orderService;

    public OrderDataFetcher(OrderService orderService) {
        this.orderService = orderService;
    }

    @DgsQuery
    public OrderResponse orderById(@InputArgument("id") Long id) {
        return orderService.findOrderById(id);
    }

    @DgsQuery
    public PagedResponse<OrderResponse> orders(
            @InputArgument("orderId") Long orderId,
            @InputArgument("page") int page,
            @InputArgument("size") int size
    ) {
        return orderService.findAllOrders(orderId, page, size);
    }

    @DgsData(parentType = "Order", field = "dishes")
    public List<OrderItemResponse> dishes(DataFetchingEnvironment dfe) {
        OrderResponse order = dfe.getSource();
        return order.getDishes();
    }

    @DgsMutation
    public OrderResponse createOrder(@InputArgument("input") Map<String, Object> input) {
        List<Map<String, Object>> dishes = (List<Map<String, Object>>) input.get("dishes");
        List<OrderItemRequest> items = dishes.stream()
                .map(d -> new OrderItemRequest(
                        Long.parseLong(d.get("dishId").toString()),
                        (Integer) d.get("quantity")
                ))
                .toList();

        return orderService.createOrder(new OrderRequest(items));
    }

    @DgsMutation
    public OrderResponse updateOrder(
            @InputArgument("id") Long id,
            @InputArgument("input") Map<String, Object> input
    ) {
        List<Map<String, Object>> dishes = (List<Map<String, Object>>) input.get("dishes");
        List<OrderItemRequest> items = dishes.stream()
                .map(d -> new OrderItemRequest(
                        Long.parseLong(d.get("dishId").toString()),
                        (Integer) d.get("quantity")
                ))
                .toList();

        return orderService.updateOrder(id, new OrderRequest(items));
    }

    @DgsMutation
    public OrderResponse updateOrderStatus(
            @InputArgument("id") Long id,
            @InputArgument("status") String status
    ) {
        return orderService.updateOrderStatus(id, status);
    }

    @DgsMutation
    public Long deleteOrder(@InputArgument Long id) {
        orderService.deleteOrder(id);
        return id;
    }
}