package demo.storage;

import demo.dto.DishResponse;
import demo.dto.IngredientResponse;
import demo.dto.OrderItemResponse;
import demo.dto.OrderResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
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

        IngredientResponse ing1 = new IngredientResponse(
                ingredientSequence.incrementAndGet(),
                "Морковь",
                100,
                "гр"
        );

        IngredientResponse ing2 = new IngredientResponse(
                ingredientSequence.incrementAndGet(),
                "Картофель",
                300,
                "гр"
        );

        IngredientResponse ing3 = new IngredientResponse(
                ingredientSequence.incrementAndGet(),
                "Лук",
                100,
                "г"
        );

        ingredients.put(ing1.getId(), ing1);
        ingredients.put(ing2.getId(), ing2);
        ingredients.put(ing3.getId(), ing3);


        IngredientResponse dishIng1 = new IngredientResponse(
                ing1.getId(),
                ing1.getName(),
                50,
                ing1.getUnit()
        );
        IngredientResponse dishIng2 = new IngredientResponse(
                ing2.getId(),
                ing2.getName(),
                150,
                ing2.getUnit()
        );
        IngredientResponse dishIng3 = new IngredientResponse(
                ing3.getId(),
                ing3.getName(),
                30,
                ing3.getUnit()
        );

        List<IngredientResponse> dishIngredients = List.of(dishIng1, dishIng2, dishIng3);
        DishResponse dish1 = new DishResponse(
                dishSequence.incrementAndGet(),
                "Овощное рагу",
                dishIngredients
        );

        dishes.put(dish1.getId(), dish1);


        OrderItemResponse orderItem1 = new OrderItemResponse(
                dish1.getId(),
                dish1.getName(),
                2
        );

        OrderResponse order1 = new OrderResponse(
                orderSequence.incrementAndGet(),
                List.of(orderItem1),
                "CREATED",
                LocalDateTime.now()
        );

        orders.put(order1.getId(), order1);
    }
}
