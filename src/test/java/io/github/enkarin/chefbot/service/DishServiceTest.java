package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DishServiceTest extends TestBase {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DishService dishService;

    @BeforeEach
    void init() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        dishService.initDishName(USER_ID, "Рагу");
    }

    @Test
    void initDishShouldWork() {
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .satisfies(u -> {
                    assertThat(u.getEditabledDish()).isNotNull();
                    assertThat(u.getEditabledDish().getDishName()).isEqualTo("Рагу");
                    assertThat(u.getEditabledDish().getType()).isNull();
                    assertThat(u.getEditabledDish().isSpicy()).isFalse();
                });
    }

    @Test
    void exitToMainMenuNotShouldRemoveEditableDish() {
        userService.switchToNewStatus(USER_ID, ChatStatus.MAIN_MENU);

        assertThat(dishRepository.findAll()).extracting(Dish::getDishName).contains("Рагу");
    }

    @Test
    void tryCreateDishWithExistsNameInCurrentUserShouldThrowException() {
        assertThatThrownBy(() -> dishService.initDishName(USER_ID, "Рагу"))
                .isInstanceOf(DishNameAlreadyExistsInCurrentUserException.class)
                .hasMessage("Рагу уже было добавлено вами ранее");
    }

    @Test
    void findById() {
        final long dishId = dishRepository.save(Dish.builder().dishName("Каша").build()).getId();

        assertThat(dishRepository.findById(dishId).orElseThrow()).satisfies(dishDto -> {
            assertThat(dishDto.getDishName()).isEqualTo("Каша");
            assertThat(dishDto.getType()).isNull();
            assertThat(dishDto.isSpicy()).isFalse();
        });

        dishRepository.deleteById(dishId);
    }

    @Test
    void initDishNameReuse() {
        final long dishId = userService.findUser(USER_ID).getEditabledDish().getId();

        dishService.initDishName(USER_ID, "Каша");

        assertThat(dishRepository.findById(dishId).orElseThrow()).satisfies(dishDto -> {
            assertThat(dishDto.getDishName()).isEqualTo("Каша");
            assertThat(dishDto.getType()).isNull();
            assertThat(dishDto.isSpicy()).isFalse();
        });
        assertThat(dishRepository.count()).isEqualTo(1);
    }

    @Test
    void deleteEditableDishShouldWork() {
        dishService.deleteEditableDish(USER_ID);

        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getEditabledDish, User::getChatStatus)
                .containsOnly(null, ChatStatus.MAIN_MENU);
        assertThat(dishRepository.count()).isEqualTo(0);
    }

    @Test
    void deleteEditableDishWithoutEditableDishShouldWork() {
        dishService.deleteEditableDish(USER_ID);

        dishService.deleteEditableDish(USER_ID);

        assertThat(userService.findUser(USER_ID).getEditabledDish()).isNull();
        assertThat(dishRepository.count()).isEqualTo(0);
    }

    @Test
    void putDishSpicy() {
        dishService.putDishIsSpicy(USER_ID);

        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().isSpicy()).isTrue();
    }

    @Test
    void putDishSoup() {
        dishService.putDishType(USER_ID, DishType.SOUP);

        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getType()).isEqualTo(DishType.SOUP);
    }

    @Test
    void putDishCuisine() {
        dishService.putDishCuisine(USER_ID, WorldCuisine.SLAVIC);

        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getCuisine()).isEqualTo(WorldCuisine.SLAVIC);
    }

    @Test
    void putDishFoodstuff() {
        dishService.putDishFoodstuff(USER_ID, "Овсянка", "Три ведра укропа");

        final String dishId = userService.findUser(USER_ID).getEditabledDish().getDishName();
        assertThat(jdbcTemplate.queryForList(
                "select p.product_name from t_dish d " +
                        "inner join t_dish_product dp on d.dish_id=dp.dish_id " +
                        "inner join t_product p on dp.product_id=p.product_name where d.dish_name=?",
                String.class,
                dishId)).containsOnly("Овсянка", "Три ведра укропа");
        assertThat(productRepository.count()).isEqualTo(2);
    }
}
