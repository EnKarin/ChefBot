package io.github.enkarin.chefbot.exceptions;

import java.io.Serial;

public class DishNameAlreadyExistsInCurrentUserException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2465550838827626776L;

    public DishNameAlreadyExistsInCurrentUserException(final String dishName) {
        super(dishName.concat(" уже было добавлено вами ранее"));
    }
}
