package demo.rabbitmq;

import demo.events.OrderAnalyzedEvent;
import demo.websocket.NotificationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    private final NotificationHandler handler;

    public NotificationListener(NotificationHandler handler) {
        this.handler = handler;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "q.notifications.browser", durable = "true"),
                    exchange = @Exchange(name = "analytics-fanout", type = "fanout")
            )
    )
    public void handleOrderAnalyzed(OrderAnalyzedEvent event) {
        log.info("Received event from RabbitMQ: {}", event);

        String message = String.format(
                "{\"type\":\"ORDER_ANALYZED\",\"orderId\":%d,\"totalItems\":%d,\"complexityScore\":%d,\"verdict\":\"%s\"}",
                event.orderId(),
                event.totalItems(),
                event.complexityScore(),
                event.verdict()
        );

        handler.broadcast(message);
    }
}
