package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExcludeUserProductsServiceTest extends TestBase {
    @Autowired
    private ExcludeUserProductsService excludeUserProductsService;

    @Autowired
    private SearchFilterService searchFilterService;

    @Autowired
    private SearchFilterRepository searchFilterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SearchProductService searchProductService;

    @Autowired
    private DishService dishService;

    @BeforeEach
    void initUser() {
        createUser(ChatStatus.MAIN_MENU);
        initDishes();
    }

    @Test
    void findExcludeProducts() {
        jdbcTemplate.update("insert into user_exclude_product(user_id, product_name) values (?, 'firstProduct'), (?, 'secondProduct')", USER_ID, USER_ID);

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("firstProduct", "secondProduct");
    }

    @Test
    void addExcludeProductsByFullName() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "firstProduct");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("firstProduct");
    }

    @Test
    void addExcludeProductsByPartialName() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "se");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("secondProduct", "seventhProduct");
    }

    @Test
    void multiplyAddExcludeProducts() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "third", "fourth");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("thirdProduct", "fourthProduct");
    }

    @Test
    void addExcludeProductsMustIgnoreCase() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "SECONDpr");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("secondProduct");
    }

    @Test
    void deleteExcludeProductsByEqualsNames() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "first", "second", "third");

        excludeUserProductsService.deleteExcludeProductsByEqualsNames(USER_ID, "firstProduct", "second");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("secondProduct", "thirdProduct");
        assertThat(productRepository.findAll()).extracting(Product::getProductName).contains("firstProduct");
    }

    @Test
    void deleteExcludeProductsByEqualsNamesMustIgnoreCase() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "first", "second", "third");

        excludeUserProductsService.deleteExcludeProductsByEqualsNames(USER_ID, "secondPRODUCT");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("firstProduct", "thirdProduct");
        assertThat(productRepository.findAll()).extracting(Product::getProductName).contains("secondProduct");
    }

    @Test
    void deleteExcludeProductsByLikeName() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "first", "second", "third");

        excludeUserProductsService.deleteExcludeProductsByLikeName(USER_ID, "firstProduct", "second");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("thirdProduct");
        assertThat(productRepository.findAll()).extracting(Product::getProductName).contains("firstProduct", "secondProduct");
    }

    @Test
    void deleteExcludeProductsByLikeNameMustIgnoreCase() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "first", "second", "third");

        excludeUserProductsService.deleteExcludeProductsByLikeName(USER_ID, "PRODUCT");

        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).isEmpty();
        assertThat(productRepository.findAll()).extracting(Product::getProductName).contains("firstProduct", "secondProduct", "thirdProduct");
    }

    @Test
    void searchPublicDishWithExcludeProducts() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setSpicy(true);
        searchFilterRepository.save(searchFilter);
        excludeUserProductsService.addExcludeProducts(USER_ID, "secondProduct");

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).satisfies(displayDishDtos -> {
            assertThat(displayDishDtos).extracting(DisplayDishDto::getDishName).containsOnly("sixth", "fourth");
            assertThat(displayDishDtos.stream().flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
                    .containsOnly("sixthProduct", "fourthProduct");
        });
    }

    @Test
    void searchPersonalDishWithExcludeProducts() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(false);
        searchFilterRepository.save(searchFilter);
        excludeUserProductsService.addExcludeProducts(USER_ID, "se");

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).satisfies(displayDishDtos -> {
            assertThat(displayDishDtos).extracting(DisplayDishDto::getDishName).containsOnly("sixth", "fifth");
            assertThat(displayDishDtos.stream().flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
                    .containsOnly("sixthProduct", "fifthProduct");
        });
    }

    @Test
    void searchPublicRecipeWithExcludeProducts() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setNeedGetRecipe(true);
        searchFilterRepository.save(searchFilter);
        excludeUserProductsService.addExcludeProducts(USER_ID, "seventhProduct");

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).satisfies(displayDishDtos -> {
            assertThat(displayDishDtos).extracting(DisplayDishDto::getDishName).containsOnly("first");
            assertThat(displayDishDtos.stream().flatMap(displayDishDto -> displayDishDto.getProductsName().stream())).containsOnly("firstProduct");
        });
    }

    @Test
    void searchPersonalRecipeWithExcludeProducts() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(false);
        searchFilter.setNeedGetRecipe(true);
        searchFilterRepository.save(searchFilter);
        excludeUserProductsService.addExcludeProducts(USER_ID, "seventhProduct");

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).isEmpty();
    }

    @Test
    void searchDishByProductsWithExcludeProducts() {
        excludeUserProductsService.addExcludeProducts(USER_ID, "seventh");

        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "se");

        assertThat(dishService.findDishByProduct(USER_ID)).allSatisfy(displayDishDto -> {
            assertThat(displayDishDto.getDishName()).isEqualTo("second");
            assertThat(displayDishDto.getProductsName()).containsOnly("secondProduct");
        });
    }

    @Test
    void searchRandomPublishDishWishExcludeProducts() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setSpicy(false);
        searchFilter.setDishType(DishType.SALAD);
        searchFilter.setCuisine(WorldCuisine.ASIA);
        searchFilter.setNeedGetRecipe(true);
        searchFilterRepository.save(searchFilter);
        initDishes();
        excludeUserProductsService.addExcludeProducts(USER_ID, "firstProduct");

        assertThatThrownBy(() -> searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).isInstanceOf(DishesNotFoundException.class);
    }
}
