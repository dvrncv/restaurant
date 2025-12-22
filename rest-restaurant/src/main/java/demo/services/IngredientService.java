package demo.services;


import demo.dto.DishResponse;
import demo.dto.IngredientRequest;
import demo.dto.IngredientResponse;
import demo.exception.IngredientAlreadyExistsException;
import demo.exception.ResourceNotFoundException;
import demo.storage.InMemoryStorage;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private final InMemoryStorage storage;

    public IngredientService(InMemoryStorage storage) {
        this.storage = storage;
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
            Long ingredientId = ingredientInDish.getId();
            IngredientResponse stored = storage.ingredients.get(ingredientId);

            if (stored == null) {
                throw new IllegalStateException("Ингредиент не найден на складе: " + ingredientInDish.getName());
            }

            int totalRequired = ingredientInDish.getQuantity() * quantity;
            if (stored.getQuantity() < totalRequired) {
                throw new IllegalStateException("Недостаточно ингредиентов: " + ingredientInDish.getName());
            }

            storage.ingredients.put(ingredientId, new IngredientResponse(
                    stored.getId(),
                    stored.getName(),
                    stored.getQuantity() - totalRequired,
                    stored.getExpirationDate(),
                    stored.getUnit()
            ));
        }
    }

    public IngredientResponse createIngredient(IngredientRequest request) {
        validateIngredientName(request.name(), null);

        long id = storage.ingredientSequence.incrementAndGet();
        IngredientResponse ingredient = new IngredientResponse(
                id,
                request.name(),
                request.quantity(),
                request.expirationDate(),
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
                request.expirationDate(),
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
