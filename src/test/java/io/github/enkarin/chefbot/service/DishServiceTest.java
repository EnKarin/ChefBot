package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.repository.ProductRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class DishServiceTest extends TestBase {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DishService dishService;

    @BeforeEach
    void init() {
        userService.createOfUpdateUser(USER_ID, CHAT_ID, USERNAME);
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
                    assertThat(u.getEditabledDish().isSoup()).isFalse();
                    assertThat(u.getEditabledDish().isSpicy()).isFalse();
                });
    }

    @Test
    void findById() {
        final long dishId = dishRepository.save(Dish.builder().dishName("Каша").build()).getId();

        assertThat(dishService.findById(dishId)).satisfies(dishDto -> {
            assertThat(dishDto).isNotNull();
            assertThat(dishDto.getName()).isEqualTo("Каша");
            assertThat(dishDto.isSoup()).isFalse();
            assertThat(dishDto.isSpicy()).isFalse();
        });

        dishRepository.deleteById(dishId);
    }

    @Test
    void initDishNameReuse() {
        final long dishId = userService.findUser(USER_ID).getEditabledDish().getId();

        dishService.initDishName(USER_ID, "Каша");

        assertThat(dishService.findById(dishId)).satisfies(dishDto -> {
            assertThat(dishDto).isNotNull();
            assertThat(dishDto.getName()).isEqualTo("Каша");
            assertThat(dishDto.isSoup()).isFalse();
            assertThat(dishDto.isSpicy()).isFalse();
        });
        assertThat(dishRepository.count()).isEqualTo(1);
    }

    @Test
    void deleteDishShouldWork() {
        dishService.deleteDish(USER_ID);

        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getEditabledDish, User::getChatStatus)
                .containsOnly(null, ChatStatus.MAIN_MENU);
        assertThat(dishRepository.count()).isEqualTo(0);
    }

    @Test
    void putDishSpicy() {
        dishService.putDishIsSpicy(USER_ID);

        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().isSpicy()).isTrue();
    }

    @Test
    void putDishSoup() {
        dishService.putDishIsSoup(USER_ID);

        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().isSoup()).isTrue();
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
        assertThat(productRepository.findAll()).hasSize(2);
    }
}
