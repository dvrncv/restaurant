package demo.exception;

public class IngredientAlreadyExistsException extends RuntimeException{
    public IngredientAlreadyExistsException(String name) {
        super("Ingredient with name='" + name + "' already exists");
    }
}
