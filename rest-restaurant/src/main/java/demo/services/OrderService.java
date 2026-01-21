package demo.services;

import demo.config.RabbitMQConfig;
import demo.dto.*;
import demo.events.OrderCreatedEvent;
import demo.events.OrderItem;
import demo.events.OrderReadyEvent;
import demo.exception.ResourceNotFoundException;
import demo.storage.InMemoryStorage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class OrderService {

    private final InMemoryStorage storage;
    private final DishService dishService;
    private final IngredientService ingredientService;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(
            InMemoryStorage storage,
            @Lazy DishService dishService,
            @Lazy IngredientService ingredientService,
            RabbitTemplate rabbitTemplate
    ) {
        this.storage = storage;
        this.dishService = dishService;
        this.ingredientService = ingredientService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public OrderResponse findOrderById(Long id) {
        return Optional.ofNullable(storage.orders.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    public PagedResponse<OrderResponse> findAllOrders(Long orderId, int page, int size) {
        Stream<OrderResponse> stream = storage.orders.values().stream()
                .sorted(Comparator.comparing(OrderResponse::getId));

        if (orderId != null) {
            stream = stream.filter(order -> order.getId().equals(orderId));
        }

        List<OrderResponse> allOrders = stream.toList();

        int totalElements = allOrders.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<OrderResponse> pageContent =
                (fromIndex > toIndex) ? List.of() : allOrders.subList(fromIndex, toIndex);

        return new PagedResponse<>(
                pageContent,
                page,
                size,
                totalElements,
                totalPages,
                page >= totalPages - 1
        );
    }

    public OrderResponse createOrder(OrderRequest request) {
        List<OrderItemResponse> items = request.dishes().stream()
                .map(dishItem -> {
                    DishResponse dish = dishService.findDishById(dishItem.dishId());
                    ingredientService.consumeIngredients(dish, dishItem.quantity());

                    return new OrderItemResponse(
                            dish.getId(),
                            dish.getName(),
                            dishItem.quantity()
                    );
                })
                .toList();

        long id = storage.orderSequence.incrementAndGet();
        LocalDateTime startTime = LocalDateTime.now();

        OrderResponse order = new OrderResponse(
                id,
                items,
                "CREATE",
                startTime
        );

        storage.orders.put(id, order);
        List<OrderItem> orderItems = items.stream()
                .map(item -> new OrderItem(
                        item.getDishId(),
                        item.getName(),
                        item.getQuantity()
                ))
                .toList();

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                orderItems,
                startTime,
                order.getStatus()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_ORDER_CREATED,
                event
        );

        return order;
    }

    public OrderResponse updateOrder(Long id, OrderRequest request) {
        OrderResponse existingOrder = findOrderById(id);

        List<OrderItemResponse> items = request.dishes().stream()
                .map(dishItem -> {
                    DishResponse dish = dishService.findDishById(dishItem.dishId());
                    ingredientService.consumeIngredients(dish, dishItem.quantity());

                    return new OrderItemResponse(
                            dish.getId(),
                            dish.getName(),
                            dishItem.quantity()
                    );
                })
                .toList();

        LocalDateTime startTime = existingOrder.getStartTime();

        OrderResponse updated = new OrderResponse(
                id,
                items,
                existingOrder.getStatus(),
                startTime
        );

        storage.orders.put(id, updated);
        return updated;
    }

    public OrderResponse updateOrderStatus(Long id, String newStatus) {
        OrderResponse existingOrder = findOrderById(id);

        if ("COMPLETED".equalsIgnoreCase(existingOrder.getStatus()) || "READY".equalsIgnoreCase(existingOrder.getStatus())) {
            throw new IllegalStateException("Нельзя изменить статус готового или завершённого заказа");
        }
        OrderResponse updatedOrder = new OrderResponse(
                id,
                existingOrder.getDishes(),
                newStatus,
                existingOrder.getStartTime(),
                LocalDateTime.now()
        );

        storage.orders.put(id, updatedOrder);

        if ("READY".equalsIgnoreCase(newStatus) || "COMPLETED".equalsIgnoreCase(newStatus)) {

            List<OrderItem> orderItems = updatedOrder.getDishes().stream()
                    .map(item -> new OrderItem(
                            item.getDishId(),
                            item.getName(),
                            item.getQuantity()
                    ))
                    .toList();

            OrderReadyEvent event = new OrderReadyEvent(
                    updatedOrder.getId(),
                    orderItems,
                    LocalDateTime.now(),
                    newStatus
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_ORDER_READY,
                    event
            );
        }

        return updatedOrder;
    }

    public void deleteOrder(Long id) {
        findOrderById(id);
        storage.orders.remove(id);
    }
}


