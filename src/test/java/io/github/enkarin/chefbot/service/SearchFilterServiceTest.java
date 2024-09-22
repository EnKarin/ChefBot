package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.SearchProduct;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import io.github.enkarin.chefbot.repository.SearchProductRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchFilterServiceTest extends TestBase {
    @Autowired
    private SearchFilterService searchFilterService;

    @Autowired
    private SearchFilterRepository searchFilterRepository;

    @Autowired
    private SearchProductRepository searchProductRepository;

    @BeforeEach
    void initUser() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
    }

    @Test
    void createSearchFilter() {
        searchFilterService.createSearchFilter(USER_ID);

        assertThat(userService.findUser(USER_ID).getSearchFilter()).satisfies(searchFilter -> {
            assertThat(searchFilter.getDishType()).isNull();
            assertThat(searchFilter.getSpicy()).isNull();
            assertThat(searchFilter.isSearchFromPublicDish()).isFalse();
            assertThat(searchFilter.getCuisine()).isNull();
            assertThat(searchFilter.isNeedGetRecipe()).isFalse();
        });
    }

    @Test
    void deleteSearchFilter() {
        searchFilterService.createSearchFilter(USER_ID);

        searchFilterService.deleteSearchFilter(USER_ID);

        assertThat(userService.findUser(USER_ID).getSearchFilter()).isNull();
        assertThat(searchFilterRepository.count()).isEqualTo(0);
    }

    @Test
    void putDishType() {
        searchFilterService.createSearchFilter(USER_ID);

        searchFilterService.putDishType(USER_ID, DishType.PASTRY);

        assertThat(userService.findUser(USER_ID).getSearchFilter().getDishType()).isEqualTo(DishType.PASTRY);
    }

    @Test
    void putSpicySign() {
        searchFilterService.createSearchFilter(USER_ID);

        searchFilterService.putSpicySign(USER_ID, false);

        assertThat(userService.findUser(USER_ID).getSearchFilter().getSpicy()).isFalse();
    }

    @Test
    void putKitchen() {
        searchFilterService.createSearchFilter(USER_ID);

        searchFilterService.putKitchen(USER_ID, WorldCuisine.INTERNATIONAL);

        assertThat(userService.findUser(USER_ID).getSearchFilter().getCuisine()).isEqualTo(WorldCuisine.INTERNATIONAL);
    }

    @Test
    void putNeedPublicSearch() {
        searchFilterService.createSearchFilter(USER_ID);

        searchFilterService.putNeedPublicSearch(USER_ID, true);

        assertThat(userService.findUser(USER_ID).getSearchFilter().isSearchFromPublicDish()).isTrue();
    }

    @Test
    void searchPublicDishWithFullFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setSpicy(false);
        searchFilter.setDishType(DishType.SALAD);
        searchFilter.setCuisine(WorldCuisine.ASIA);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).satisfies(displayDishDtos -> {
            assertThat(displayDishDtos).extracting(DisplayDishDto::getDishName).containsOnly("first");
            assertThat(displayDishDtos).extracting(DisplayDishDto::getProductsName).allMatch(set -> set.contains("firstProduct"));
        });
    }

    @Test
    void searchPublicDishWithPartialFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setCuisine(WorldCuisine.INTERNATIONAL);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).satisfies(displayDishDtos -> {
            assertThat(displayDishDtos).extracting(DisplayDishDto::getDishName).containsOnly("second");
            assertThat(displayDishDtos).extracting(DisplayDishDto::getProductsName).allMatch(set -> set.contains("secondProduct"));
        });
    }

    @Test
    void searchPublicRecipeWithPartialFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setNeedGetRecipe(true);
        searchFilter.setCuisine(WorldCuisine.ASIA);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).anySatisfy(displayDishDto -> {
            assertThat(displayDishDto.getDishName()).isEqualTo("first");
            assertThat(displayDishDto.getProductsName()).contains("firstProduct");
            assertThat(displayDishDto.toString()).isEqualTo("""
                    *first:*
                    -firstProduct
                    Рецепт приготовления:
                    Тушить в казане""");
        });
    }

    @Test
    void searchCurrentUserDishWithCurrentFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(false);
        searchFilter.setCuisine(WorldCuisine.MIDDLE_EASTERN);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).satisfies(displayDishDtos -> {
            assertThat(displayDishDtos).extracting(DisplayDishDto::getDishName).containsOnly("fifth");
            assertThat(displayDishDtos).extracting(DisplayDishDto::getProductsName).allMatch(set -> set.contains("fifthProduct"));
        });
    }

    @Test
    void searchDishWishNullableCuisineFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setSpicy(true);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).satisfies(displayDishDtos -> {
            assertThat(displayDishDtos).extracting(DisplayDishDto::getDishName).containsOnly("second", "sixth", "fourth");
            assertThat(displayDishDtos.stream().flatMap(displayDishDto -> displayDishDto.getProductsName().stream()))
                    .containsOnly("secondProduct", "sixthProduct", "fourthProduct");
        });
    }

    @Test
    void paginationInSearchPublicDishWithCurrentFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        searchFilterService.putNeedPublicSearch(USER_ID, true);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).extracting(DisplayDishDto::getDishName)
                .containsOnly("first", "second", "third", "fourth", "fifth");
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).extracting(DisplayDishDto::getDishName).containsOnly("sixth", "seventh");
    }

    @Test
    void paginationInSearchCurrentUserDishWithCurrentFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        searchFilterService.putNeedPublicSearch(USER_ID, false);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).extracting(DisplayDishDto::getDishName).containsOnly("fifth", "sixth", "seventh");
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).isEmpty();
    }

    @Test
    void noRepetitionsInSearchCurrentUserDishWithCurrentFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        searchFilterService.putNeedPublicSearch(USER_ID, false);
        initDishes();
        dishRepository.save(Dish.builder()
                .dishName("eighth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .products(Set.of(productRepository.save(Product.builder().productName("eighthProduct").build())))
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("ninth")
                .type(DishType.MAIN_DISH)
                .spicy(true)
                .cuisine(WorldCuisine.MEDITERRANEAN)
                .products(Set.of(productRepository.save(Product.builder().productName("ninthProduct").build())))
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("tenth")
                .type(DishType.SALAD)
                .spicy(false)
                .cuisine(WorldCuisine.OTHER)
                .products(Set.of(productRepository.save(Product.builder().productName("tenthProduct").build())))
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());

        final Set<String> firstResult = searchFilterService.searchDishWithCurrentFilter(USER_ID).stream().map(DisplayDishDto::getDishName).collect(Collectors.toSet());
        final Set<String> secondResult = searchFilterService.searchDishWithCurrentFilter(USER_ID).stream().map(DisplayDishDto::getDishName).collect(Collectors.toSet());
        final int firstResultOriginSize = firstResult.size();
        firstResult.removeAll(secondResult);

        assertThat(firstResult).hasSize(firstResultOriginSize);
    }

    @Test
    void searchPublicRandomDishWithCurrentFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setSpicy(false);
        searchFilter.setDishType(DishType.SALAD);
        searchFilter.setCuisine(WorldCuisine.ASIA);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).satisfies(displayDishDto -> {
            assertThat(displayDishDto).extracting(DisplayDishDto::getDishName).isEqualTo("first");
            assertThat(displayDishDto.getProductsName()).containsOnly("firstProduct");
        });
    }

    @Test
    void searchPublicRandomDishWithCurrentFilterWithRecipe() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setSpicy(false);
        searchFilter.setDishType(DishType.SALAD);
        searchFilter.setCuisine(WorldCuisine.ASIA);
        searchFilter.setNeedGetRecipe(true);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).satisfies(displayDishDto -> {
            assertThat(displayDishDto).extracting(DisplayDishDto::getDishName).isEqualTo("first");
            assertThat(displayDishDto.getProductsName()).containsOnly("firstProduct");
            assertThat(displayDishDto.toString()).isEqualTo("""
                    *first:*
                    -firstProduct
                    Рецепт приготовления:
                    Тушить в казане""");
        });
    }

    @Test
    void searchPersonalRandomDishWithCurrentFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(false);
        searchFilter.setSpicy(false);
        searchFilter.setDishType(DishType.SOUP);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).satisfies(displayDishDto -> {
            assertThat(displayDishDto).extracting(DisplayDishDto::getDishName).isEqualTo("fifth");
            assertThat(displayDishDto.getProductsName()).containsOnly("fifthProduct");
        });
    }

    @Test
    void searchPersonalRandomDishWithCurrentFilterWithRecipe() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(false);
        searchFilter.setSpicy(false);
        searchFilter.setDishType(DishType.SALAD);
        searchFilter.setNeedGetRecipe(true);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).satisfies(displayDishDto -> {
            assertThat(displayDishDto).extracting(DisplayDishDto::getDishName).isEqualTo("seventh");
            assertThat(displayDishDto.getProductsName()).containsOnly("seventhProduct");
            assertThat(displayDishDto.toString()).isEqualTo("""
                    *seventh:*
                    -seventhProduct
                    Рецепт приготовления:
                    Дать настояться месяцок""");
        });
    }

    @Test
    void searchPublicRandomDishWithCurrentFilterMustThrowExceptionIfDishesNotFound() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setSpicy(true);
        searchFilter.setDishType(DishType.SALAD);
        searchFilter.setCuisine(WorldCuisine.ASIA);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThatThrownBy(() -> searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).isInstanceOf(DishesNotFoundException.class);
    }

    @Test
    void searchPersonalRandomDishWithCurrentFilterMustThrowExceptionIfDishesNotFound() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(false);
        searchFilter.setSpicy(true);
        searchFilter.setDishType(DishType.SOUP);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThatThrownBy(() -> searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).isInstanceOf(DishesNotFoundException.class);
    }

    @Test
    void createSearchFilterForFindRecipe() {
        searchFilterService.createSearchFilterForFindRecipe(USER_ID);

        assertThat(userService.findUser(USER_ID).getSearchFilter()).satisfies(searchFilter -> {
            assertThat(searchFilter.getDishType()).isNull();
            assertThat(searchFilter.getSpicy()).isNull();
            assertThat(searchFilter.isSearchFromPublicDish()).isFalse();
            assertThat(searchFilter.getCuisine()).isNull();
            assertThat(searchFilter.isNeedGetRecipe()).isTrue();
        });
    }

    @Test
    void searchPublishDishWithNeedRecipeFlagReturnDishOnlyWithRecipe() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setNeedGetRecipe(true);
        searchFilter.setCuisine(WorldCuisine.ASIA);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).allSatisfy(displayDishDto -> {
            assertThat(displayDishDto).extracting(DisplayDishDto::getDishName).isEqualTo("first");
            assertThat(displayDishDto.getProductsName()).containsOnly("firstProduct");
            assertThat(displayDishDto.toString()).isEqualTo("""
                    *first:*
                    -firstProduct
                    Рецепт приготовления:
                    Тушить в казане""");
        });
    }

    @Test
    void searchPersonalDishWithNeedRecipeFlagReturnDishOnlyWithRecipe() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(false);
        searchFilter.setNeedGetRecipe(true);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).allSatisfy(displayDishDto -> {
            assertThat(displayDishDto).extracting(DisplayDishDto::getDishName).isEqualTo("seventh");
            assertThat(displayDishDto.getProductsName()).containsOnly("seventhProduct");
            assertThat(displayDishDto.toString()).isEqualTo("""
                    *seventh:*
                    -seventhProduct
                    Рецепт приготовления:
                    Дать настояться месяцок""");
        });
    }

    @Test
    void searchRandomPublishDishWithNeedRecipeFlagReturnDishOnlyWithRecipe() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(true);
        searchFilter.setNeedGetRecipe(true);
        searchFilter.setCuisine(WorldCuisine.ASIA);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).satisfies(displayDishDto -> {
            assertThat(displayDishDto).extracting(DisplayDishDto::getDishName).isEqualTo("first");
            assertThat(displayDishDto.getProductsName()).containsOnly("firstProduct");
            assertThat(displayDishDto.toString()).isEqualTo("""
                    *first:*
                    -firstProduct
                    Рецепт приготовления:
                    Тушить в казане""");
        });
    }

    @Test
    void searchRandomPersonalDishWithNeedRecipeFlagReturnDishOnlyWithRecipe() {
        searchFilterService.createSearchFilter(USER_ID);
        final SearchFilter searchFilter = searchFilterRepository.findAll().get(0);
        searchFilter.setSearchFromPublicDish(false);
        searchFilter.setNeedGetRecipe(true);
        searchFilterRepository.save(searchFilter);
        initDishes();

        assertThat(searchFilterService.searchRandomDishWithCurrentFilter(USER_ID)).satisfies(displayDishDto -> {
            assertThat(displayDishDto).extracting(DisplayDishDto::getDishName).isEqualTo("seventh");
            assertThat(displayDishDto.getProductsName()).containsOnly("seventhProduct");
            assertThat(displayDishDto.toString()).isEqualTo("""
                    *seventh:*
                    -seventhProduct
                    Рецепт приготовления:
                    Дать настояться месяцок""");
        });
    }

    @Test
    void saveProductsForCurrentSearchFilter() {
        searchFilterService.createSearchFilter(USER_ID);

        searchFilterService.saveProductsForCurrentSearchFilter(USER_ID, "Три ведра укропа", "Ведро воды");

        assertThat(searchProductRepository.findAll()).extracting(SearchProduct::getName).containsOnly("Три ведра укропа", "Ведро воды");
    }

    @Test
    void saveProductsForCurrentSearchFilterMustParseInput() {
        searchFilterService.createSearchFilter(USER_ID);

        searchFilterService.saveProductsForCurrentSearchFilter(USER_ID, "три ведра укропа", "ведро Воды");

        assertThat(searchProductRepository.findAll()).extracting(SearchProduct::getName).containsOnly("Три ведра укропа", "Ведро воды");
    }

    @Test
    void dropPageNumber() {
        searchFilterService.createSearchFilter(USER_ID);
        initDishes();
        searchFilterService.searchRandomDishWithCurrentFilter(USER_ID);

        searchFilterService.dropPageNumberValue(USER_ID);

        assertThat(searchFilterRepository.findAll()).allMatch(searchFilter -> searchFilter.getPageNumber() == 0);
    }
}
