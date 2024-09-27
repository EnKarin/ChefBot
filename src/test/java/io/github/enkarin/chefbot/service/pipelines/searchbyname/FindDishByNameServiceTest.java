package io.github.enkarin.chefbot.service.pipelines.searchbyname;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.searchbyname.FindDishByNameService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FindDishByNameServiceTest extends TestBase {
    @Autowired
    private FindDishByNameService findDishByNameService;

    @Test
    void execute() {
        createUser(ChatStatus.FIND_DISH_BY_NAME);
        initDishes();

        assertThat(findDishByNameService.execute(USER_ID, "Se")).satisfies(executionResult -> {
            assertThat(executionResult.chatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
            assertThat((List<DisplayDishDto>) executionResult.systemAction().orElseThrow())
                    .extracting(DisplayDishDto::getDishName)
                    .containsOnly("second", "seventh");
        });
    }
}
