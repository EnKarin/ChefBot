package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_SOUP;
import static io.github.enkarin.chefbot.enums.ChatStatus.SELECT_DISH_SPICY;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingSearchSoupServiceTest extends TestBase {

    @Autowired
    private ProcessingSearchSoupService searchSoupService;

    @BeforeEach
    void setUp() {
        createUser(SELECT_DISH_SOUP);
    }

    @ParameterizedTest
    @MethodSource("provideTextAndResult")
    void executeShouldWork(final String text, final ChatStatus status, final Boolean isSoup) {
        assertThat(searchSoupService.execute(USER_ID, text))
                .isEqualTo(status);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .extracting(SearchFilter::getSoup)
                .isEqualTo(isSoup);
    }

    @Test
    void executeShouldWorkWithUnknownInput() {
        assertThat(searchSoupService.execute(USER_ID, "unknown"))
                .isEqualTo(SELECT_DISH_SOUP);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .isNull();
    }

    static Stream<Arguments> provideTextAndResult() {
        return Stream.of(
                Arguments.of("Да", SELECT_DISH_SPICY, true),
                Arguments.of("Нет", SELECT_DISH_SPICY, false)
        );
    }
}