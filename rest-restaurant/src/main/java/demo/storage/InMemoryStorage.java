package demo.storage;

import demo.dto.DishResponse;
import demo.dto.IngredientResponse;
import demo.dto.OrderItemResponse;
import demo.dto.OrderResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    public final Map<Long, DishResponse> dishes = new ConcurrentHashMap<>();
    public final Map<Long, OrderResponse> orders = new ConcurrentHashMap<>();
    public final Map<Long, IngredientResponse> ingredients = new ConcurrentHashMap<>();

    public final AtomicLong dishSequence = new AtomicLong(0);
    public final AtomicLong orderSequence = new AtomicLong(0);
    public final AtomicLong ingredientSequence = new AtomicLong(0);

    @PostConstruct
    public void init(){

        IngredientResponse ing1 = new IngredientResponse(ingredientSequence.incrementAndGet(),
                "Морковь",
                10000,
                LocalDate.now().plusDays(10),
                "гр"
        );

        IngredientResponse ing2 = new IngredientResponse(
                ingredientSequence.incrementAndGet(),
                "Картофель",
                300,
                LocalDate.now().plusDays(7),
                "гр"
        );

        IngredientResponse ing3 = new IngredientResponse(
                ingredientSequence.incrementAndGet(),
                "Лук",
                100,
                LocalDate.now().plusDays(5),
                "г"
        );

        ingredients.put(ing1.getId(), ing1);
        ingredients.put(ing2.getId(), ing2);
        ingredients.put(ing3.getId(), ing3);


        List<IngredientResponse> dishIngredients = List.of(ing1, ing2, ing3);
        DishResponse dish1 = new DishResponse(
                dishSequence.incrementAndGet(),
                "Овощное рагу",
                25,
                dishIngredients
        );

        dishes.put(dish1.getId(), dish1);


        OrderItemResponse orderItem1 = new OrderItemResponse(
                dish1.getId(),
                dish1.getName(),
                2,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(dish1.getDurationTime())
        );

        OrderResponse order1 = new OrderResponse(
                orderSequence.incrementAndGet(),
                List.of(orderItem1),
                "READY",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(dish1.getDurationTime())
        );

        orders.put(order1.getId(), order1);
    }
}
