package io.github.enkarin.chefbot.service.pipelines.search;

import io.github.enkarin.chefbot.controllers.pipelines.search.ExecuteRandomSearchService;
import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ExecuteRandomSearchServiceTest extends TestBase {
    @Autowired
    private ExecuteRandomSearchService randomSearchService;

    @Autowired
    private SearchFilterRepository searchFilterRepository;

    @Test
    void executeShouldWorkWithBackToMainMenu() {
        userRepository.save(User.builder()
                .id(USER_ID)
                .chatId(CHAT_ID)
                .chatStatus(ChatStatus.MAIN_MENU)
                .searchFilter(searchFilterRepository.save(new SearchFilter()))
                .build());

        assertThat(randomSearchService.execute(USER_ID, "вернуться в главное меню").chatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(searchFilterRepository.count()).isEqualTo(0);
        assertThat(userRepository.findById(USER_ID))
                .isPresent()
                .get()
                .extracting(User::getSearchFilter)
                .isNull();
    }

    @Test
    void executeShouldWork() {
        assertThat(randomSearchService.execute(USER_ID, "Вывести ещё").chatStatus()).isEqualTo(ChatStatus.EXECUTE_RANDOM_SEARCH);
    }
}
