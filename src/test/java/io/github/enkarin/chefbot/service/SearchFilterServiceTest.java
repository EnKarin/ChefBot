package io.github.enkarin.chefbot.service;

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
}