package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.capitalize;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DishService {
    private final ProductRepository productRepository;
    private final DishRepository dishRepository;
    private final UserService userService;

    @Transactional
    public void initDishName(final long userId, final String name) {
        final User user = userService.findUser(userId);
        if (currentUserNotContainDishWithSpecifiedName(name, user)) {
            if (isNull(user.getEditabledDish())) {
                initNewDish(name, user);
            } else {
                renameCreatingDish(name, user);
            }
        } else {
            throw new DishNameAlreadyExistsInCurrentUserException(name);
        }
    }

    @Transactional
    public void renameCreatingDish(final long userId, final String name) {
        final User user = userService.findUser(userId);
        if (currentUserNotContainDishWithSpecifiedName(name, user)) {
            renameCreatingDish(name, user);
        } else {
            throw new DishNameAlreadyExistsInCurrentUserException(name);
        }
    }

    private void renameCreatingDish(final String name, final User owner) {
        owner.getEditabledDish().setDishName(name);
    }

    private void initNewDish(final String name, final User owner) {
        owner.setEditabledDish(dishRepository.save(Dish.builder()
                .dishName(name)
                .owner(owner)
                .build()));
    }

    private boolean currentUserNotContainDishWithSpecifiedName(final String name, final User user) {
        return user.getDishes().stream().map(Dish::getDishName).noneMatch(n -> n.equals(name));
    }

    @Transactional
    public void putDishIsSpicy(final long userId) {
        final Dish dish = findEditableDish(userId);
        dish.setSpicy(true);
    }

    @Transactional
    public void putDishIsNotSpicy(final long userId) {
        final Dish dish = findEditableDish(userId);
        dish.setSpicy(false);
    }

    @Transactional
    public void putDishType(final long userId, final DishType dishType) {
        final Dish dish = findEditableDish(userId);
        dish.setType(dishType);
    }

    @Transactional
    public void putDishCuisine(final long userId, final WorldCuisine cuisine) {
        final Dish dish = findEditableDish(userId);
        dish.setCuisine(cuisine);
    }

    @Transactional
    public void putDishFoodstuff(final long userId, final String... foodstuffNames) {
        final Dish dish = findEditableDish(userId);
        dish.getProducts().clear();
        for (final String foodstuffName : foodstuffNames) {
            final String trimFoodstuff = capitalize(foodstuffName.trim().toLowerCase(Locale.ROOT));
            dish.getProducts().add(productRepository.findById(trimFoodstuff).orElseGet(() -> productRepository.save(Product.builder().productName(trimFoodstuff).build())));
        }
    }

    @Transactional
    public void putDishRecipe(final long userId, final String recipe) {
        final Dish dish = findEditableDish(userId);
        dish.setRecipe(recipe);
    }

    public String[] findDishNamesWithoutRecipeForUser(final long userId) {
        return userService.findUser(userId).getDishes().stream()
                .filter(dish -> isNull(dish.getRecipe()))
                .limit(10)
                .map(Dish::getDishName)
                .toArray(String[]::new);
    }

    @Transactional
    public void putEditableDish(final long userId, final String dishName) {
        final User user = userService.findUser(userId);
        user.setEditabledDish(user.getDishes().stream()
                .filter(dish -> dish.getDishName().equalsIgnoreCase(dishName))
                .findAny()
                .orElseThrow(DishesNotFoundException::new));
    }

    @Transactional
    public void dropPublishFlagForEditableDish(final long userId) {
        findEditableDish(userId).setPublished(false);
    }

    public List<DisplayDishDto> searchDishByName(final String nameSubstring) {
        return dishRepository.findByDishNameContainingIgnoreCase(nameSubstring).stream()
                .map(dish -> new DisplayDishDto(dish.getDishName(), dish.getProducts().stream().map(Product::getProductName).collect(Collectors.toSet())))
                .toList();
    }

    public boolean editableDishWasPublish(final long userId) {
        return findEditableDish(userId).isPublished();
    }

    private Dish findEditableDish(final long userId) {
        return userService.findUser(userId).getEditabledDish();
    }
}
