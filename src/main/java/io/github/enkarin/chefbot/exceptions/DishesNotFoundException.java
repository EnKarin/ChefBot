package io.github.enkarin.chefbot.exceptions;

import java.io.Serial;

public class DishesNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8457039661466489936L;

    public DishesNotFoundException() {
        super("Подходящие блюда не найдены. Вы возвращены в главное меню.");
    }
}
