package demo.listeners;

import demo.events.IngredientStockEvent;
import demo.events.OrderCreatedEvent;
import demo.events.OrderReadyEvent;
import demo.events.OrderAnalyzedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

import com.rabbitmq.client.Channel;

@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private static final String EXCHANGE_NAME = "order-exchange";
    private static final String ORDER_QUEUE = "q.audit.orders";
    private static final String INGREDIENT_QUEUE = "q.audit.ingredients";
    private static final String ANALYTICS_QUEUE = "q.audit.analytics";

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = ORDER_QUEUE,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.orders")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "order.created"
            )
    )

    public void handleOrderCreatedEvent(
            @Payload OrderCreatedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {

        try {
            log.info("Получено событие: ЗАКАЗ СОЗДАН");
            log.info("Идентификатор заказа: {}", event.orderId());
            log.info("Статус заказа: {}", event.status());
            log.info("Время создания заказа: {}", event.startTime());

            log.info("Позиции в заказе:");
            event.items().forEach(item ->
                    log.info(
                            "  Блюдо: '{}', количество: {}",
                            item.dishName(),
                            item.quantity()
                    )
            );
            log.info("Событие создания заказа успешно обработано");
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Ошибка при обработке события создания заказа. Идентификатор заказа: {}",
                    event.orderId(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "notification-queue.dlq", durable = "true"),
                    exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
                    key = "dlq.notifications"
            )
    )
    public void handleDlqMessages(Object failedMessage) {
        log.error("!!! Received message in DLQ: {}", failedMessage);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = ORDER_QUEUE,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.orders")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "order.ready"
            )
    )

    public void handleOrderReadyEvent(
            @Payload OrderReadyEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {

        try {
            log.info("Получено событие: ЗАКАЗ ГОТОВ");
            log.info("Идентификатор заказа: {}", event.orderId());
            log.info("Статус заказа: {}", event.status());
            log.info("Время готовности заказа: {}", event.readyTime());

            log.info("Позиции в заказе:");
            event.items().forEach(item ->
                    log.info(
                            "  Блюдо: '{}', количество: {}",
                            item.dishName(),
                            item.quantity()
                    )
            );

            log.info("Событие готовности заказа успешно обработано");
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Ошибка при обработке события готовности заказа. Идентификатор заказа: {}",
                    event.orderId(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = INGREDIENT_QUEUE,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.ingredients")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "ingredient.critical"
            )
    )

    public void handleCriticalIngredientStockEvent(
            @Payload IngredientStockEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {

        try {
            log.warn("Получено событие: КРИТИЧЕСКИЙ УРОВЕНЬ ЗАПАСОВ ИНГРЕДИЕНТА");
            log.warn("Идентификатор ингредиента: {}", event.ingredientId());
            log.warn("Название ингредиента: {}", event.ingredientName());
            log.warn("Текущее количество: {}", event.currentQuantity());
            log.warn("Критический порог: {}", event.criticalThreshold());

            log.warn("Событие критического запаса ингредиента обработано");
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Ошибка при обработке события критического запаса ингредиента. Идентификатор ингредиента: {}",
                    event.ingredientId(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = ANALYTICS_QUEUE,
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            name = "analytics-fanout",
                            type = "fanout"
                    )
            )
    )
    public void handleOrderStatistics(OrderAnalyzedEvent event) {
        log.info("Получено событие: АНАЛИТИКА ЗАКАЗА");
        log.info("Номер заказа: {}", event.orderId());
        log.info("Время начала заказа: {}", event.orderStartTime());
        log.info("Время готовности заказа: {}", event.orderReadyTime());
        log.info("Количество блюд: {}", event.dishCount());
        log.info("Общее количество позиций: {}", event.totalItems());
        log.info("Среднее время на блюдо: {} мин", event.averageTimePerDish());
        log.info("Оценка сложности: {}", event.complexityScore());

        if (!event.recommendations().isEmpty()) {
            log.info("Рекомендации:");
            for (String rec : event.recommendations()) {
                log.info(" - {}", rec);
            }
        }
    }
}