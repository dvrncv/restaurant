package demo.services;

import demo.config.RabbitMQConfig;
import demo.dto.*;
import demo.events.OrderCreatedEvent;
import demo.events.OrderDeletedEvent;
import demo.events.OrderItem;
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

                    LocalDateTime now = LocalDateTime.now();
                    return new OrderItemResponse(
                            dish.getId(),
                            dish.getName(),
                            dishItem.quantity(),
                            now,
                            now.plusMinutes(dish.getDurationTime())
                    );
                })
                .toList();

        long id = storage.orderSequence.incrementAndGet();
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = items.stream()
                .map(OrderItemResponse::getFinishedAt)
                .max(LocalDateTime::compareTo)
                .orElse(startTime);

        OrderResponse order = new OrderResponse(
                id,
                items,
                "CREATE",
                startTime,
                endTime
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

                    LocalDateTime now = LocalDateTime.now();
                    return new OrderItemResponse(
                            dish.getId(),
                            dish.getName(),
                            dishItem.quantity(),
                            now,
                            now.plusMinutes(dish.getDurationTime())
                    );
                })
                .toList();

        LocalDateTime startTime = existingOrder.getStartTime();
        LocalDateTime endTime = items.stream()
                .map(OrderItemResponse::getFinishedAt)
                .max(LocalDateTime::compareTo)
                .orElse(startTime);

        OrderResponse updated = new OrderResponse(
                id,
                items,
                existingOrder.getStatus(),
                startTime,
                endTime
        );

        storage.orders.put(id, updated);
        return updated;
    }

    public OrderResponse updateOrderStatus(Long id, String newStatus) {
        OrderResponse existingOrder = findOrderById(id);

        if ("COMPLETED".equalsIgnoreCase(existingOrder.getStatus())) {
            throw new IllegalStateException("Нельзя изменить статус завершённого заказа");
        }

        OrderResponse updated = new OrderResponse(
                id,
                existingOrder.getDishes(),
                newStatus,
                existingOrder.getStartTime(),
                existingOrder.getEndTime()
        );

        storage.orders.put(id, updated);
        return updated;
    }

    public void deleteOrder(Long id) {
        findOrderById(id);
        storage.orders.remove(id);
        OrderDeletedEvent event = new OrderDeletedEvent(id);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ORDER_DELETED, event);
    }
}
