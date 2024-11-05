package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.dto.DisplayDishWithRecipeDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.ProductQuantity;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishNameAlreadyExistsInCurrentUserException;
import io.github.enkarin.chefbot.util.ModerationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DishServiceTest extends ModerationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DishService dishService;

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private SearchProductService searchProductService;

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
    void putAllDishFoodstuff() {
        dishService.putAllDishFoodstuff(USER_ID, Map.of("Овсянка", "", "Три ведра укропа", ""));

        final String dishId = userService.findUser(USER_ID).getEditabledDish().getDishName();
        assertThat(jdbcTemplate.queryForList(
                "select p.product_name from t_dish d " +
                        "inner join t_dish_product dp on d.dish_id=dp.dish_id " +
                        "inner join t_product p on dp.product_id=p.product_name where d.dish_name=?",
                String.class,
                dishId)).containsOnly("Овсянка", "Три ведра укропа");
        assertThat(productRepository.count()).isEqualTo(2);
    }

    @Test
    void putAllDishFoodstuffWithQuantity() {
        dishService.putAllDishFoodstuff(USER_ID, Map.of("Овсянка", "10 грамм", "Отруби", "1 ведро"));

        assertThat(productRepository.findAll()).extracting(Product::getProductName).containsOnly("Овсянка", "Отруби");
        assertThat(productQuantityRepository.findAll()).extracting(ProductQuantity::getQuantityProduct).containsOnly("10 грамм", "1 ведро");
    }

    @Test
    void putRecipe() {
        dishService.putDishRecipe(USER_ID, "Тушить два часа");

        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getRecipe()).isEqualTo("Тушить два часа");
    }

    @Test
    void findDishesWithoutRecipeForUser() {
        initDishes();

        assertThat(dishService.findDishNamesWithoutRecipeForUser(USER_ID)).containsOnly("fifth", "sixth", "Рагу");
    }

    @Test
    void findDishesWithoutRecipeForUserPaginationShouldWork() {
        initDishes();

        dishService.findDishNamesWithoutRecipeForUser(USER_ID);
        assertThat(dishService.findDishNamesWithoutRecipeForUser(USER_ID)).isEmpty();
    }

    @Test
    void putEditableDish() {
        initDishes();
        dishService.putEditableDish(USER_ID, "fifth");

        assertThat(userService.findUser(USER_ID).getEditabledDish().getDishName()).isEqualTo("fifth");
    }

    @Test
    void putNonPublishFlag() {
        dishService.dropPublishFlagForEditableDish(USER_ID);

        assertThat(userService.findUser(USER_ID).getEditabledDish().isPublished()).isFalse();
    }

    @Test
    void findFalsePublishFlag() {
        assertThat(dishService.editableDishWasPublish(USER_ID)).isFalse();
    }

    @Test
    void findTruePublishFlag() {
        final Dish dish = userService.findUser(USER_ID).getEditabledDish();
        dish.setPublished(true);
        dishRepository.save(dish);

        assertThat(dishService.editableDishWasPublish(USER_ID)).isTrue();
    }

    @Test
    void searchByName() {
        initDishes();

        assertThat(dishService.findDishByName(USER_ID, "fi")).extracting(DisplayDishDto::getDishName).containsOnly("first", "fifth");
    }

    @Test
    void searchDishWithRecipeByName() {
        initDishes();

        assertThat(dishService.findDishByName(USER_ID, "seventh")).extracting(DisplayDishDto::toString).containsOnly("""
                *seventh:*
                -seventhProduct
                Рецепт приготовления:
                Дать настояться месяцок""");
    }

    @Test
    void searchByNameNotExistsDish() {
        initDishes();

        assertThat(dishService.findDishByName(USER_ID, "dummy")).isEmpty();
    }

    @Test
    void searchByNameMustNotFoundOtherPrivateDish() {
        userService.createOrUpdateUser(USER_ID - 1, CHAT_ID - 1, USERNAME + 1);
        initDishes();

        assertThat(dishService.findDishByName(USER_ID - 1, "fifth")).isEmpty();
    }

    @Test
    void findDishByProductsWithOneFilter() {
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "firstProduct");

        assertThat(searchProductService.findDishByProduct(USER_ID)).allSatisfy(displayDishDto -> {
            assertThat(displayDishDto.getDishName()).isEqualTo("first");
            assertThat(displayDishDto.getProductsName()).containsOnly("firstProduct");
        });
    }

    @Test
    void findDishByProductsIgnoreCase() {
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "Second", "product");

        assertThat(searchProductService.findDishByProduct(USER_ID)).allSatisfy(displayDishDto -> {
            assertThat(displayDishDto.getDishName()).isEqualTo("second");
            assertThat(displayDishDto.getProductsName()).containsOnly("secondProduct");
        });
    }

    @Test
    void findDishByProductsWithManyFilters() {
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "se", "th", "product");

        assertThat(searchProductService.findDishByProduct(USER_ID)).allSatisfy(displayDishDto -> {
            assertThat(displayDishDto.getDishName()).isEqualTo("seventh");
            assertThat(displayDishDto.getProductsName()).containsOnly("seventhProduct");
        });
    }

    @Test
    void findDishByProductsWithTooManyFilters() {
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "firstProduct", "secondProduct");

        assertThat(searchProductService.findDishByProduct(USER_ID)).isEmpty();
    }

    @Test
    void manyFindDishByProduct() {
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "firstProduct");

        assertThat(searchProductService.findDishByProduct(USER_ID)).allSatisfy(displayDishDto -> {
            assertThat(displayDishDto.getDishName()).isEqualTo("first");
            assertThat(displayDishDto.getProductsName()).containsOnly("firstProduct");
        });
        assertThat(searchProductService.findDishByProduct(USER_ID)).isEmpty();
    }

    @Test
    void findDishWithRecipeByProduct() {
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "seventhProduct");

        assertThat(searchProductService.findDishByProduct(USER_ID)).allSatisfy(displayDishDto -> {
            assertThat(displayDishDto.getDishName()).isEqualTo("seventh");
            assertThat(displayDishDto.getProductsName()).containsOnly("seventhProduct");
            assertThat(((DisplayDishWithRecipeDto) displayDishDto).getRecipe()).isEqualTo("Дать настояться месяцок");
        });
    }

    @Test
    void deleteDish() {
        userService.switchToNewStatus(USER_ID, ChatStatus.NEW_DISH_NAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.MAIN_MENU);

        dishService.deleteDish(USER_ID, "рагу");

        assertThat(dishRepository.findAll()).extracting(Dish::getDishName).doesNotContain("Рагу");
    }

    @Test
    void deleteDishMustNotDropProduct() {
        userService.switchToNewStatus(USER_ID, ChatStatus.NEW_DISH_NAME);
        dishService.putAllDishFoodstuff(USER_ID, Map.of("Картошка", "4 штуки", "Кабачок", "1 штука"));
        userService.switchToNewStatus(USER_ID, ChatStatus.MAIN_MENU);

        dishService.deleteDish(USER_ID, "рагу");

        assertThat(dishRepository.findAll()).extracting(Dish::getDishName).doesNotContain("Рагу");
        assertThat(productRepository.findAll()).extracting(Product::getProductName).containsOnly("Картошка", "Кабачок");
    }

    @Test
    void deleteDishAfterCreateModerationRequest() {
        userService.switchToNewStatus(USER_ID, ChatStatus.NEW_DISH_NAME);
        moderationService.createModerationRequest(USER_ID);
        userService.switchToNewStatus(USER_ID, ChatStatus.MAIN_MENU);

        dishService.deleteDish(USER_ID, "Рагу");

        assertThat(dishRepository.findAll()).extracting(Dish::getDishName).doesNotContain("Рагу");
    }

    @Test
    void deleteDishAfterStartModeration() {
        clear();
        moderationInit();
        moderationService.startModerate(USER_ID - 1, moderationRequestsId[0]);

        dishService.deleteDish(USER_ID, "firstDish");

        assertThat(dishRepository.findAll()).extracting(Dish::getDishName).doesNotContain("firstDish");
    }

    @Test
    void searchMustNotFoundOtherPrivateDish() {
        userService.createOrUpdateUser(USER_ID - 1, CHAT_ID - 1, USERNAME + 1);
        initDishes();
        searchProductService.saveProductsForCurrentSearchFilter(USER_ID - 1, "seventhProduct");

        assertThat(searchProductService.findDishByProduct(USER_ID - 1)).isEmpty();
    }
}
