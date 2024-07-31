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
        userService.findOrSaveUser(CHAT_ID);
        dishService.initDishName(CHAT_ID, "Рагу");
    }

    @Test
    void initDishShouldWork() {
        assertThat(userRepository.findById(CHAT_ID))
                .isPresent()
                .get()
                .satisfies(u -> {
                    assertThat(u.getChatStatus()).isEqualTo(ChatStatus.NEW_DISH_NAME);
                    assertThat(u.getEditabledDish()).isNotNull();
                    assertThat(dishRepository.findById(u.getEditabledDish().getId()))
                            .isPresent()
                            .get()
                            .extracting(Dish::getDishName)
                            .isEqualTo("Рагу");
                    assertThat(u.getEditabledDish().isSoup()).isFalse();
                    assertThat(u.getEditabledDish().isSpicy()).isFalse();
                });
    }

    @Test
    void deleteDishShouldWork() {
        dishService.deleteDish(CHAT_ID);

        assertThat(userRepository.findById(CHAT_ID))
                .isPresent()
                .get()
                .extracting(User::getEditabledDish, User::getChatStatus)
                .containsOnly(null, ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).isEmpty();
    }

    @Test
    void putDishSpicy() {
        dishService.putDishIsSpicy(CHAT_ID);

        assertThat(userRepository.findById(CHAT_ID).orElseThrow().getEditabledDish().isSpicy()).isTrue();
    }

    @Test
    void putDishSoup() {
        dishService.putDishIsSoup(CHAT_ID);

        assertThat(userRepository.findById(CHAT_ID).orElseThrow().getEditabledDish().isSoup()).isTrue();
    }

    @Test
    void putDishCuisine() {
        dishService.putDishCuisine(CHAT_ID, WorldCuisine.SLAVIC);

        assertThat(userRepository.findById(CHAT_ID).orElseThrow().getEditabledDish().getCuisine()).isEqualTo(WorldCuisine.SLAVIC);
    }

    @Test
    void putDishFoodstuff() {
        dishService.putDishFoodstuff(CHAT_ID, "Овсянка", "Три ведра укропа");

        final long dishId = userService.findUser(CHAT_ID).getEditabledDish().getId();
        assertThat(jdbcTemplate.queryForList(
                "select p.product_name from t_dish d inner join t_dish_product dp on d.id=dp.dish_id inner join t_product p on dp.product_id=p.product_name where d.id=?",
                String.class,
                dishId)).containsOnly("Овсянка", "Три ведра укропа");
        assertThat(productRepository.findAll()).hasSize(2);
    }
}
