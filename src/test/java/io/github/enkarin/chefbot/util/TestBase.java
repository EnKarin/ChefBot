package io.github.enkarin.chefbot.util;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.ProductQuantity;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.ProductQuantityRepository;
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
    @Autowired
    protected ProductQuantityRepository productQuantityRepository;

    @AfterEach
    protected void clear() {
        jdbcTemplate.execute("TRUNCATE t_user, t_dish, t_product, moderation_request, search_filter, moderation_request_message, t_dish_product, search_product CASCADE");
    }

    protected void createUser(final ChatStatus status) {
        userRepository.save(User.builder().id(USER_ID).chatStatus(status).build());
    }

    protected void initDishes() {
        productQuantityRepository.save(ProductQuantity.builder().dish(dishRepository.save(Dish.builder()
                .dishName("first")
                .type(DishType.SALAD)
                .spicy(false)
                .cuisine(WorldCuisine.ASIA)
                .published(true)
                .recipe("Тушить в казане")
                .build())).product(productRepository.save(Product.builder().productName("firstProduct").build())).build());
        productQuantityRepository.save(ProductQuantity.builder().dish(dishRepository.save(Dish.builder()
                .dishName("second")
                .type(DishType.PASTRY)
                .spicy(true)
                .cuisine(WorldCuisine.INTERNATIONAL)
                .published(true)
                .build())).product(productRepository.save(Product.builder().productName("secondProduct").build())).build());
        productQuantityRepository.save(ProductQuantity.builder().dish(dishRepository.save(Dish.builder()
                .dishName("third")
                .type(DishType.SNACK)
                .spicy(false)
                .cuisine(WorldCuisine.SLAVIC)
                .published(true)
                .build())).product(productRepository.save(Product.builder().productName("thirdProduct").build())).build());
        productQuantityRepository.save(ProductQuantity.builder().dish(dishRepository.save(Dish.builder()
                .dishName("fourth")
                .type(DishType.SOUP)
                .spicy(true)
                .cuisine(WorldCuisine.MEXICAN)
                .published(true)
                .build())).product(productRepository.save(Product.builder().productName("fourthProduct").build())).build());
        productQuantityRepository.save(ProductQuantity.builder().dish(dishRepository.save(Dish.builder()
                .dishName("fifth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build())).product(productRepository.save(Product.builder().productName("fifthProduct").build())).build());
        productQuantityRepository.save(ProductQuantity.builder().dish(dishRepository.save(Dish.builder()
                .dishName("sixth")
                .type(DishType.MAIN_DISH)
                .spicy(true)
                .cuisine(WorldCuisine.MEDITERRANEAN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build())).product(productRepository.save(Product.builder().productName("sixthProduct").build())).build());
        productQuantityRepository.save(ProductQuantity.builder().dish(dishRepository.save(Dish.builder()
                .dishName("seventh")
                .type(DishType.SALAD)
                .spicy(false)
                .cuisine(WorldCuisine.OTHER)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .recipe("Дать настояться месяцок")
                .build())).product(productRepository.save(Product.builder().productName("seventhProduct").build())).build());
    }
}
