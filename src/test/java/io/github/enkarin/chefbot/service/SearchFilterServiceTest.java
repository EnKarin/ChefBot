package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchFilterServiceTest extends TestBase {
    @Autowired
    private SearchFilterService searchFilterService;

    @Autowired
    private SearchFilterRepository searchFilterRepository;

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
            assertThat(displayDishDtos).extracting(DisplayDishDto::dishName).containsOnly("first");
            assertThat(displayDishDtos).extracting(DisplayDishDto::productsName).allMatch(set -> set.contains("firstProduct"));
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
            assertThat(displayDishDtos).extracting(DisplayDishDto::dishName).containsOnly("second");
            assertThat(displayDishDtos).extracting(DisplayDishDto::productsName).allMatch(set -> set.contains("secondProduct"));
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
            assertThat(displayDishDtos).extracting(DisplayDishDto::dishName).containsOnly("fifth");
            assertThat(displayDishDtos).extracting(DisplayDishDto::productsName).allMatch(set -> set.contains("fifthProduct"));
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
            assertThat(displayDishDtos).extracting(DisplayDishDto::dishName).containsOnly("second", "sixth", "fourth");
            assertThat(displayDishDtos.stream().flatMap(displayDishDto -> displayDishDto.productsName().stream()))
                    .containsOnly("secondProduct", "sixthProduct", "fourthProduct");
        });
    }

    @Test
    void paginationInSearchPublicDishWithCurrentFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        searchFilterService.putNeedPublicSearch(USER_ID, true);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).extracting(DisplayDishDto::dishName).containsOnly("first", "second", "third", "fourth", "fifth");
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).extracting(DisplayDishDto::dishName).containsOnly("sixth");
    }

    @Test
    void paginationInSearchCurrentUserDishWithCurrentFilter() {
        searchFilterService.createSearchFilter(USER_ID);
        searchFilterService.putNeedPublicSearch(USER_ID, false);
        initDishes();

        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).extracting(DisplayDishDto::dishName).containsOnly("fifth", "sixth");
        assertThat(searchFilterService.searchDishWithCurrentFilter(USER_ID)).isEmpty();
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
            assertThat(displayDishDto).extracting(DisplayDishDto::dishName).isEqualTo("first");
            assertThat(displayDishDto.productsName()).containsOnly("firstProduct");
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
            assertThat(displayDishDto).extracting(DisplayDishDto::dishName).isEqualTo("fifth");
            assertThat(displayDishDto.productsName()).containsOnly("fifthProduct");
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
}
