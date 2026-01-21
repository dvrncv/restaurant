package demo.services;

import demo.config.RabbitMQConfig;
import demo.dto.DishResponse;
import demo.dto.IngredientRequest;
import demo.dto.IngredientResponse;
import demo.events.IngredientStockEvent;
import demo.exception.IngredientAlreadyExistsException;
import demo.exception.ResourceNotFoundException;
import demo.storage.InMemoryStorage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private static final int CRITICAL_STOCK_THRESHOLD = 10;

    private final InMemoryStorage storage;
    private final RabbitTemplate rabbitTemplate;

    public IngredientService(InMemoryStorage storage, RabbitTemplate rabbitTemplate) {
        this.storage = storage;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<IngredientResponse> findAllIngredients() {
        return storage.ingredients.values().stream().toList();
    }

    public IngredientResponse findIngredientById(Long id) {
        return Optional.ofNullable(storage.ingredients.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
    }

    public void consumeIngredients(DishResponse dish, int quantity) {
        for (IngredientResponse ingredientInDish : dish.getIngredients()) {
            IngredientResponse storedIngredient = findStoredIngredient(ingredientInDish);
            
            int requiredAmount = ingredientInDish.getQuantity() * quantity;
            validateSufficientStock(storedIngredient, requiredAmount);
            
            int remainingStock = reduceIngredientStock(storedIngredient, requiredAmount);
            
            if (remainingStock <= CRITICAL_STOCK_THRESHOLD) {
                notifyCriticalStock(storedIngredient, remainingStock);
            }
        }
    }

    private IngredientResponse findStoredIngredient(IngredientResponse ingredient) {
        IngredientResponse stored = storage.ingredients.get(ingredient.getId());
        if (stored == null) {
            throw new IllegalStateException(
                    "Ингредиент не найден на складе: " + ingredient.getName()
            );
        }
        return stored;
    }

    private void validateSufficientStock(IngredientResponse ingredient, int required) {
        if (ingredient.getQuantity() < required) {
            throw new IllegalStateException(
                    "Недостаточно ингредиентов: " + ingredient.getName() + 
                    " (требуется: " + required + ", доступно: " + ingredient.getQuantity() + ")"
            );
        }
    }

    private int reduceIngredientStock(IngredientResponse ingredient, int amount) {
        int newQuantity = ingredient.getQuantity() - amount;
        
        IngredientResponse updated = new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                newQuantity,
                ingredient.getUnit()
        );
        
        storage.ingredients.put(ingredient.getId(), updated);
        return newQuantity;
    }

    private void notifyCriticalStock(IngredientResponse ingredient, int currentStock) {
        IngredientStockEvent event = new IngredientStockEvent(
                ingredient.getId(),
                ingredient.getName(),
                currentStock,
                CRITICAL_STOCK_THRESHOLD
        );
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_INGREDIENT_CRITICAL,
                event
        );
    }

    public IngredientResponse createIngredient(IngredientRequest request) {
        validateIngredientName(request.name(), null);

        long id = storage.ingredientSequence.incrementAndGet();
        IngredientResponse ingredient = new IngredientResponse(
                id,
                request.name(),
                request.quantity(),
                request.unit()
        );
        storage.ingredients.put(id, ingredient);
        return ingredient;
    }

    public IngredientResponse updateIngredient(Long id, IngredientRequest request) {
        findIngredientById(id);
        validateIngredientName(request.name(), id);

        IngredientResponse updatedIngredient = new IngredientResponse(
                id,
                request.name(),
                request.quantity(),
                request.unit()
        );
        storage.ingredients.put(id, updatedIngredient);
        return updatedIngredient;
    }

    public void deleteIngredient(Long id) {
        findIngredientById(id);
        storage.ingredients.remove(id);
    }

    private void validateIngredientName(String name, Long currentId) {
        storage.ingredients.values().stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .filter(i -> !i.getId().equals(currentId))
                .findAny()
                .ifPresent(i -> { throw new IngredientAlreadyExistsException(name); });
    }
}
