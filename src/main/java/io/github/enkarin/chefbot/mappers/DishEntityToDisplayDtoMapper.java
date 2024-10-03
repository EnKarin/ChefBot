package io.github.enkarin.chefbot.mappers;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.dto.DisplayDishWithRecipeDto;
import io.github.enkarin.chefbot.entity.Dish;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class DishEntityToDisplayDtoMapper {

    public DisplayDishWithRecipeDto mapWithRecipe(final Dish dish) {
        return new DisplayDishWithRecipeDto(dish.getDishName(), findProductsInfo(dish), dish.getRecipe());
    }

    public DisplayDishDto mapWithoutRecipe(final Dish dish) {
        return new DisplayDishDto(dish.getDishName(), findProductsInfo(dish));
    }

    private Set<String> findProductsInfo(final Dish dish) {
        return dish.getProducts().stream()
                .map(productQuantity -> productQuantity.getProduct()
                        .getProductName()
                        .concat(isNull(productQuantity.getQuantityProduct()) || productQuantity.getQuantityProduct().isEmpty()
                                ? ""
                                : ": ".concat(productQuantity.getQuantityProduct())))
                .collect(Collectors.toSet());
    }
}
