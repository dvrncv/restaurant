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
        System.out.println("\nАНАЛИТИКА ЗАКАЗА");
        System.out.println("Номер заказа: " + event.orderId());
        System.out.println("Создан: " + event.orderStartTime());
        System.out.println("Готов: " + event.orderReadyTime());
        System.out.println("Количество блюд: " + event.dishCount());
        System.out.println("Общее количество позиций: " + event.totalItems());
        System.out.println("Оценка сложности: " + event.complexityScore());
        System.out.println("Среднее время на блюдо: " + event.averageTimePerDish() + " мин");

        if (!event.recommendations().isEmpty()) {
            System.out.println("Рекомендации: " + String.join("; ", event.recommendations()));
        }
    }
}
