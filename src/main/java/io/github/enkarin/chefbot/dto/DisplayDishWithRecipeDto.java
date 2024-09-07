package io.github.enkarin.chefbot.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class DisplayDishWithRecipeDto extends DisplayDishDto {
    private final String recipe;

    public DisplayDishWithRecipeDto(final String dishName, final Set<String> productsName, final String recipe) {
        super(dishName, productsName);
        this.recipe = recipe;
    }

    @Override
    public String toString() {
        return super.toString().concat("\nРецепт приготовления:\n").concat(recipe);
    }
}
