package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

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
        if (currentUserContainDishWithSpecifiedName(name, user)) {
            if (isNull(user.getEditabledDish())) {
                initNewDish(name, user);
            } else {
                renameCreatingDish(name, user);
            }
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

    private boolean currentUserContainDishWithSpecifiedName(final String name, final User user) {
        return user.getDishes().stream().map(Dish::getDishName).noneMatch(n -> n.equals(name));
    }

    @Transactional
    public void deleteEditableDish(final long userId) {
        final User user = userService.findUser(userId);
        final Dish deletedDish = user.getEditabledDish();
        if (Objects.nonNull(deletedDish)) {
            user.setEditabledDish(null);
            dishRepository.delete(deletedDish);
        }
    }

    @Transactional
    public void putDishIsSpicy(final long userId) {
        final Dish dish = findEditableDish(userId);
        dish.setSpicy(true);
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
        final Set<Product> products = new HashSet<>();
        for (final String foodstuffName : foodstuffNames) {
            final String trimFoodstuff = capitalize(foodstuffName.trim().toLowerCase(Locale.ROOT));
            products.add(productRepository.findById(trimFoodstuff).orElseGet(() -> productRepository.save(Product.builder().productName(trimFoodstuff).build())));
        }
        dish.setProducts(products);
    }

    @Transactional
    public void putDishRecipe(final long userId, final String recipe) {
        final Dish dish = findEditableDish(userId);
        dish.setRecipe(recipe);
    }

    private Dish findEditableDish(final long userId) {
        return userService.findUser(userId).getEditabledDish();
    }
}
