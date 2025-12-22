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

        try {
            OrderAnalyticsRequest.Builder requestBuilder = OrderAnalyticsRequest.newBuilder()
                    .setOrderId(order.getId())
                    .setCookingTimeMinutes(
                            order.getDishes().stream()
                                    .mapToInt(d -> (int) java.time.Duration.between(
                                            d.getStartedAt(),
                                            d.getFinishedAt()
                                    ).toMinutes())
                                    .sum()
                    );

            for (OrderItemResponse item : order.getDishes()) {
                requestBuilder.addItems(
                        OrderItem.newBuilder()
                                .setDishId(item.getDishId())
                                .setQuantity(item.getQuantity())
                                .build()
                );
            }

            var response = analyticsStub.analyzeOrder(requestBuilder.build());

            OrderAnalyzedEvent event = new OrderAnalyzedEvent(
                    response.getOrderId(),
                    response.getTotalItems(),
                    response.getComplexityScore(),
                    response.getVerdict()
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
                    .body("Analytics service unavailable");
        }
    }
}
