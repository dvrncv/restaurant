package demo.listeners;

import demo.events.OrderAnalyzedEvent;
import demo.events.OrderCreatedEvent;
import demo.events.OrderDeletedEvent;
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
    private static final String QUEUE_NAME = "notification-queue";

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "order.created"
            )
    )
    // Используем @Payload для явного указания параметра сообщения
    public void handleOrderCreatedEvent(
            @Payload OrderCreatedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {

        try {
            log.info("==== НОВЫЙ ЗАКАЗ СОЗДАН ====");
            log.info("Номер заказа: {}", event.orderId());
            log.info("Статус: {}", event.status());
            log.info("Время создания: {}", event.startTime());

            log.info("Состав заказа:");
            event.items().forEach(item ->
                    log.info(
                            "  - dishId={}, name='{}', quantity={}",
                            item.dishId(),
                            item.dishName(),
                            item.quantity()
                    )
            );
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Ошибка обработки заказа {}. Отправляем в DLQ",
                    event.orderId(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "order.deleted"
            )
    )
    public void handleOrderDeletedEvent(
            @Payload OrderDeletedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {

        try {
            log.info("==== ЗАКАЗ УДАЛЁН ====");
            log.info("Номер заказа: {}", event.orderId());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error(
                    "Ошибка обработки OrderDeletedEvent для заказа {}. Отправляем в DLQ",
                    event.orderId(),
                    e
            );

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
                            name = "q.audit.order.analytics",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            name = "order-analytics-fanout",
                            type = "fanout"
                    )
            )
    )
    public void handle(OrderAnalyzedEvent event) {
        log.info(
                "AUDIT: Order {} analyzed. Score={}, verdict={}",
                event.orderId(),
                event.complexityScore(),
                event.verdict()
        );
    }
}

