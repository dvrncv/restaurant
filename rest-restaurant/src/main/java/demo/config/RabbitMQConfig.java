package demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "restaurant-exchange";
    public static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    public static final String ROUTING_KEY_ORDER_READY = "order.ready";
    public static final String ROUTING_KEY_INGREDIENT_CRITICAL = "ingredient.critical";
    public static final String FANOUT_EXCHANGE = "analytics-fanout";

    @Bean
    public FanoutExchange analyticsExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(new ObjectMapper().findAndRegisterModules());
    }
}
