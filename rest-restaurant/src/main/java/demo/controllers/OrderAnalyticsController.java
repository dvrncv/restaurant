package demo.controllers;

import demo.AnalyticsServiceGrpc;
import demo.OrderAnalyticsRequest;
import demo.OrderItem;
import demo.config.RabbitMQConfig;
import demo.events.OrderAnalyzedEvent;
import demo.storage.InMemoryStorage;
import demo.dto.OrderResponse;
import demo.dto.OrderItemResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderAnalyticsController {

    @GrpcClient("analytics-service")
    private AnalyticsServiceGrpc.AnalyticsServiceBlockingStub analyticsStub;

    private final RabbitTemplate rabbitTemplate;
    private final InMemoryStorage storage;

    public OrderAnalyticsController(RabbitTemplate rabbitTemplate, InMemoryStorage storage) {
        this.rabbitTemplate = rabbitTemplate;
        this.storage = storage;
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<?> analyze(@PathVariable Long id) {

        OrderResponse order = storage.orders.get(id);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Order not found");
        }

        String status = order.getStatus();
        if (!"READY".equalsIgnoreCase(status) && !"COMPLETED".equalsIgnoreCase(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Заказ ещё в процессе приготовления (статус: " + status + "). " +
                            "Аналитика доступна только для заказов со статусом READY или COMPLETED.");
        }

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = order.getStartTime();
            LocalDateTime completionTime = order.getEndTime();

            if ("READY".equalsIgnoreCase(status)) {
                completionTime = now;
            }

            int actualCookingTime = (int) Duration.between(startTime, completionTime).toMinutes();

            List<String> ingredientsUsed = new ArrayList<>();
            order.getDishes().forEach(dishItem -> {
                var dish = storage.dishes.get(dishItem.getDishId());
                if (dish != null && dish.getIngredients() != null) {
                    dish.getIngredients().forEach(ing -> {
                        int totalUsed = dishItem.getQuantity() * ing.getQuantity();
                        ingredientsUsed.add(ing.getName() + ": " + totalUsed + " " + ing.getUnit());
                    });
                }
            });

            OrderAnalyticsRequest.Builder requestBuilder = OrderAnalyticsRequest.newBuilder()
                    .setOrderId(order.getId())
                    .setCookingTimeMinutes(actualCookingTime)
                    .setOrderCreated(startTime.toString())
                    .setOrderCompleted(completionTime.toString())
                    .addAllTotalIngredientsUsed(ingredientsUsed);

            for (OrderItemResponse item : order.getDishes()) {
                requestBuilder.addItems(
                        OrderItem.newBuilder()
                                .setDishId(item.getDishId())
                                .setQuantity(item.getQuantity())
                                .setDishName(item.getName())
                                .build()
                );
            }

            var response = analyticsStub.analyzeOrder(requestBuilder.build());

            OrderAnalyzedEvent event = new OrderAnalyzedEvent(
                    response.getOrderId(),
                    response.getOrderStartTime(),
                    response.getOrderReadyTime(),
                    response.getDishCount(),
                    response.getTotalItems(),
                    response.getComplexityScore(),
                    response.getAverageTimePerDish(),
                    response.getRecommendationsList()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.FANOUT_EXCHANGE,
                    "",
                    event
            );

            return ResponseEntity.ok(event);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Analytics service unavailable: " + e.getMessage());
        }
    }
}
