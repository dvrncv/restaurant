package demo.exception;

public class DishAlreadyExistsException extends RuntimeException {
    public DishAlreadyExistsException(String name) {
        super("Dish with name='" + name + "' already exists");
    }
}