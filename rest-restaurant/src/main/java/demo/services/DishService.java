package demo.services;


import demo.dto.DishRequest;
import demo.dto.DishResponse;
import demo.dto.IngredientResponse;
import demo.exception.ResourceNotFoundException;
import demo.storage.InMemoryStorage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    private final InMemoryStorage storage;
    private final IngredientService ingredientService;

    public DishService(InMemoryStorage storage, @Lazy IngredientService ingredientService) {
        this.storage = storage;
        this.ingredientService = ingredientService;
    }

    public List<DishResponse> findAllDishes() {
        return storage.dishes.values().stream().toList();
    }

    public DishResponse findDishById(Long id) {
        return Optional.ofNullable(storage.dishes.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Dish", id));
    }


    public DishResponse createDish(DishRequest request) {
        validateDishName(request.name(), null);
        List<IngredientResponse> ingredients = request.ingredients().stream()
                .map(ingredientService::findIngredientById)
                .toList();

        long id = storage.dishSequence.incrementAndGet();
        DishResponse dish = new DishResponse(
                id,
                request.name(),
                request.durationTime(),
                ingredients
        );
        storage.dishes.put(id, dish);
        return dish;
    }

    public DishResponse updateDish(Long id, DishRequest request) {
        validateDishName(request.name(), id);
        findDishById(id);

        List<IngredientResponse> ingredients = request.ingredients().stream()
                .map(ingredientService::findIngredientById)
                .toList();

        DishResponse updatedDish = new DishResponse(
                id,
                request.name(),
                request.durationTime(),
                ingredients
        );
        storage.dishes.put(id, updatedDish);
        return updatedDish;
    }

    public void deleteDish(Long id) {
        findDishById(id);
        storage.dishes.remove(id);
    }

    private void validateDishName(String name, Long currentDishId) {
        storage.dishes.values().stream()
                .filter(dish -> dish.getName().equalsIgnoreCase(name))
                .filter(dish -> !dish.getId().equals(currentDishId))
                .findAny()
                .ifPresent(dish -> {
                    throw new RuntimeException("Dish with name '" + name + "' already exists");
                });
    }
}