package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

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
            assertThat(searchFilter.getSoup()).isNull();
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
    void putSoupSign() {
        searchFilterService.createSearchFilter(USER_ID);

        searchFilterService.putSoupSign(USER_ID, true);

        assertThat(userService.findUser(USER_ID).getSearchFilter().getSoup()).isTrue();
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
        searchFilter.setSoup(false);
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
}
