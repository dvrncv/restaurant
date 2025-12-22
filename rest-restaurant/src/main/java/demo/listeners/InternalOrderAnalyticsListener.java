package demo.listeners;

import demo.events.OrderAnalyzedEvent;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InternalOrderAnalyticsListener {

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = "q.demorest.order.analytics",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            name = "order-analytics-fanout",
                            type = "fanout"
                    )
            )
    )
    public void log(OrderAnalyzedEvent event) {
        System.out.println(
                "Order " + event.orderId() + " analyzed"
        );
    }
}
