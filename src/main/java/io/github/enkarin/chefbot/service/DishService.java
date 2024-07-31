package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DishService {
    private final ProductRepository productRepository;
    private final DishRepository dishRepository;
    private final UserService userService;

    @Transactional
    void initDishName(final long chatId, final String name) {
        final User user = userService.findUser(chatId);
        user.setEditabledDish(dishRepository.save(Dish.builder()
                .dishName(name)
                .owner(user)
                .build()
        ));
        user.setChatStatus(ChatStatus.NEW_DISH_NAME);
    }

    @Transactional
    void deleteDish(final long chatId) {
        final User user = userService.findUser(chatId);
        final long deletedDishId = user.getEditabledDish().getId();

        user.setEditabledDish(null);
        user.setChatStatus(ChatStatus.MAIN_MENU);
        dishRepository.findById(deletedDishId).ifPresent(dish -> dishRepository.deleteById(dish.getId()));
    }

    @Transactional
    void putDishIsSpicy(final long chatId) {
        final Dish dish = userService.findUser(chatId).getEditabledDish();
        dish.setSpicy(true);
        dishRepository.save(dish);
    }


    @Transactional
    void putDishIsSoup(final long chatId) {
        final Dish dish = userService.findUser(chatId).getEditabledDish();
        dish.setSoup(true);
        dishRepository.save(dish);
    }

    @Transactional
    void putDishCuisine(final long chatId, final WorldCuisine cuisine) {
        final Dish dish = userService.findUser(chatId).getEditabledDish();
        dish.setCuisine(cuisine);
        dishRepository.save(dish);
    }

    @Transactional
    void putDishFoodstuff(final long chatId, final String... foodstuffNames) {
        final Dish dish = userService.findUser(chatId).getEditabledDish();
        final Set<Product> products = new HashSet<>();
        for (final String foodstuffName : foodstuffNames) {
            products.add(productRepository.findById(foodstuffName).orElseGet(() -> productRepository.save(Product.builder().productName(foodstuffName).build())));
        }
        dish.setProducts(products);
        dishRepository.save(dish);
    }
}
