package io.github.enkarin.chefbot.util;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.ProductRepository;
import io.github.enkarin.chefbot.repository.UserRepository;
import io.github.enkarin.chefbot.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.Set;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
public class TestBase {
    protected static final Long USER_ID = Long.MAX_VALUE;
    protected static final Long CHAT_ID = Long.MAX_VALUE - 1000;
    protected static final String USERNAME = "Pupa";

    @MockBean
    protected TelegramBotsApi telegramBotApi;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected DishRepository dishRepository;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void clear() {
        jdbcTemplate.execute("TRUNCATE t_user, t_dish, t_product, moderation_request, search_filter, moderation_request_message, t_dish_product CASCADE");
    }

    protected void createUser(final ChatStatus status) {
        userRepository.save(User.builder().id(USER_ID).chatStatus(status).build());
    }

    protected void initDishes() {
        dishRepository.save(Dish.builder()
                .dishName("first")
                .type(DishType.SALAD)
                .spicy(false)
                .cuisine(WorldCuisine.ASIA)
                .products(Set.of(productRepository.save(Product.builder().productName("firstProduct").build())))
                .published(true)
                .build());
        dishRepository.save(Dish.builder()
                .dishName("second")
                .type(DishType.PASTRY)
                .spicy(true)
                .cuisine(WorldCuisine.INTERNATIONAL)
                .products(Set.of(productRepository.save(Product.builder().productName("secondProduct").build())))
                .published(true)
                .build());
        dishRepository.save(Dish.builder()
                .dishName("third")
                .type(DishType.SNACK)
                .spicy(false)
                .cuisine(WorldCuisine.SLAVIC)
                .products(Set.of(productRepository.save(Product.builder().productName("thirdProduct").build())))
                .published(true)
                .build());
        dishRepository.save(Dish.builder()
                .dishName("fourth")
                .type(DishType.SOUP)
                .spicy(true)
                .cuisine(WorldCuisine.MEXICAN)
                .products(Set.of(productRepository.save(Product.builder().productName("fourthProduct").build())))
                .published(true)
                .build());
        dishRepository.save(Dish.builder()
                .dishName("fifth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .products(Set.of(productRepository.save(Product.builder().productName("fifthProduct").build())))
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("sixth")
                .type(DishType.MAIN_DISH)
                .spicy(true)
                .cuisine(WorldCuisine.MEDITERRANEAN)
                .products(Set.of(productRepository.save(Product.builder().productName("sixthProduct").build())))
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
    }
}
