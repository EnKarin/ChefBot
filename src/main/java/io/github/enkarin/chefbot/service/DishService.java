package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final UserService userService;

    @Transactional
    public void initDishName(final Long chatId, final String name) {
        final User user = userService.findUser(chatId);
        final Dish dish = dishRepository.save(
                Dish.builder()
                        .dishName(name)
                        .owner(user)
                        .build()
        );
        user.setEditabledDish(dish);
        user.setChatStatus(ChatStatus.NEW_DISH_NAME);
    }

    @Transactional
    public void deleteDish(final Long chatId) {
        final User user = userService.findUser(chatId);
        final long deletedDishId = user.getEditabledDish().getId();

        user.setEditabledDish(null);
        user.setChatStatus(ChatStatus.MAIN_MENU);
        dishRepository.findById(deletedDishId)
                .ifPresent(dish -> dishRepository.deleteById(dish.getId()));

    }
}
