package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.ProductQuantity;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.mappers.DishEntityToDisplayDtoMapper;
import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.ProductQuantityRepository;
import io.github.enkarin.chefbot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.capitalize;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DishService {
    private final ProductRepository productRepository;
    private final DishRepository dishRepository;
    private final UserService userService;
    private final ProductQuantityRepository productQuantityRepository;
    private final DishEntityToDisplayDtoMapper dishEntityToDisplayDtoMapper;

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
    public void deleteDish(final long userId, final String name) {
        dishRepository.findByDishNameIgnoreCaseAndOwner(name, userService.findUser(userId)).ifPresentOrElse(dishForRemove -> {
            userService.deleteLinkForDish(dishForRemove);
            dishRepository.delete(dishForRemove);
        }, () -> {
            throw new DishesNotFoundException();
        });
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
    public void putAllDishFoodstuff(final long userId, final Map<String, String> foodstuffs) {
        final Dish dish = findEditableDish(userId);
        productQuantityRepository.deleteAll(dish.getProducts());
        for (final var foodstuff : foodstuffs.entrySet()) {
            final String trimFoodstuff = capitalize(foodstuff.getKey().trim().toLowerCase(Locale.ROOT));
            final Product product = productRepository.findById(trimFoodstuff).orElseGet(() -> productRepository.save(Product.builder().productName(trimFoodstuff).build()));
            productQuantityRepository.save(ProductQuantity.builder().product(product).dish(dish).quantityProduct(foodstuff.getValue().trim()).build());
        }
    }

    @Transactional
    public void putDishRecipe(final long userId, final String recipe) {
        final Dish dish = findEditableDish(userId);
        dish.setRecipe(recipe);
    }

    @Transactional
    public List<String> findDishNamesWithoutRecipeForUser(final long userId) {
        final User user = userService.findUser(userId);
        final List<String> result = user.getDishes().stream()
                .filter(dish -> isNull(dish.getRecipe()))
                .sorted(Comparator.comparing(Dish::getDishName))
                .skip(user.getSearchPageNumber() * 10L)
                .limit(10)
                .map(Dish::getDishName)
                .collect(Collectors.toList());
        user.setSearchPageNumber(user.getSearchPageNumber() + 1);
        return result;
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

    public List<DisplayDishDto> findDishByName(final long userId, final String nameSubstring) {
        return dishRepository.findByDishName(nameSubstring, userId).stream()
                .map(dish -> nonNull(dish.getRecipe()) ? dishEntityToDisplayDtoMapper.mapWithRecipe(dish) : dishEntityToDisplayDtoMapper.mapWithoutRecipe(dish))
                .toList();
    }

    public boolean editableDishWasPublish(final long userId) {
        return findEditableDish(userId).isPublished();
    }

    private Dish findEditableDish(final long userId) {
        return userService.findUser(userId).getEditabledDish();
    }
}
