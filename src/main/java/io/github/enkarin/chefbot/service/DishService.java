package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.mappers.DishEntityDtoMapper;
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
    private final DishEntityDtoMapper mapper;

    public DishDto findById(final long id) {
        return mapper.entityToDto(dishRepository.findById(id).orElseThrow());
    }

    @Transactional
    void initDishName(final long userId, final String name) {
        final User user = userService.findUser(userId);
        if (user.getEditabledDish() == null) {
            user.setEditabledDish(dishRepository.save(Dish.builder()
                    .dishName(name)
                    .owner(user)
                    .build()
            ));
        } else {
            user.getEditabledDish().setDishName(name);
        }
    }

    @Transactional
    void deleteDish(final long userId) {
        final User user = userService.findUser(userId);
        final long deletedDishId = user.getEditabledDish().getId();

        user.setEditabledDish(null);
        dishRepository.deleteById(deletedDishId);
    }

    @Transactional
    void putDishIsSpicy(final long userId) {
        final Dish dish = findEditableDish(userId);
        dish.setSpicy(true);
    }


    @Transactional
    void putDishIsSoup(final long userId) {
        final Dish dish = findEditableDish(userId);
        dish.setSoup(true);
    }

    @Transactional
    void putDishCuisine(final long userId, final WorldCuisine cuisine) {
        final Dish dish = findEditableDish(userId);
        dish.setCuisine(cuisine);
    }

    @Transactional
    void putDishFoodstuff(final long userId, final String... foodstuffNames) {
        final Dish dish = findEditableDish(userId);
        final Set<Product> products = new HashSet<>();
        for (final String foodstuffName : foodstuffNames) {
            products.add(productRepository.findById(foodstuffName).orElseGet(() -> productRepository.save(Product.builder().productName(foodstuffName).build())));
        }
        dish.setProducts(products);
    }

    private Dish findEditableDish(final long userId) {
        return userService.findUser(userId).getEditabledDish();
    }
}
