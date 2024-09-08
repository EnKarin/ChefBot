package io.github.enkarin.chefbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatStatus {
    NEW_DISH_NAME(true),
    MAIN_MENU(false),
    NEW_DISH_SPICY(true),
    NEW_DISH_KITCHEN(true),
    NEW_DISH_FOODSTUFF(true),
    NEW_DISH_NEED_PUBLISH(true),
    NEW_DISH_TYPE(true),
    SELECT_DISH_SPICY(false),
    SELECT_DISH_KITCHEN(false),
    SELECT_DISH_PRICE(false),
    REMOVE_DISH(false),
    SELECT_DISH_TYPE(false),
    SELECT_DISH_PUBLISHED(false),
    APPROVE_BACK_TO_MAIN_MENU(false),
    EXECUTE_SEARCH(false),
    EXECUTE_RANDOM_SEARCH(false),
    WRITE_DECLINE_MODERATION_REQUEST(false),
    GET_NEED_DISH_RECIPE(false),
    NEW_DISH_RECIPE(true),
    SELECT_DISH_TYPE_WITH_RECIPE_SEARCH(false),
    ENRICHING_RECIPES(false),
    EXISTS_DISH_PUT_RECIPE(false);

    private final boolean newDishStatus;
}
