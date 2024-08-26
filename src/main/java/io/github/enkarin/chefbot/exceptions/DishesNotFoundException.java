package io.github.enkarin.chefbot.exceptions;

import java.io.Serial;

public class DishesNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8457039661466489936L;

    public DishesNotFoundException() {
        super("Подходящих блюд нет. Вы возвращены в главное меню.");
    }
}
