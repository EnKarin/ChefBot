package io.github.enkarin.chefbot.pipelinehandlers.search;

import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RootProcessingSearchServiceTest {
    private final RootProcessingSearchService service = new RootProcessingSearchService();

    @Test
    void executeShouldReturnSelectDishSpicyForSpicyText() {
        final ExecutionResult result = service.execute(1L, "По остроте");

        assertThat(result.chatStatus()).isEqualTo(ChatStatus.SELECT_DISH_SPICY);
        assertThat(result.systemAction()).isEmpty();
    }

    @Test
    void executeShouldReturnSelectDishKitchenForKitchenText() {
        final ExecutionResult result = service.execute(1L, "По виду кухни");

        assertThat(result.chatStatus()).isEqualTo(ChatStatus.SELECT_DISH_KITCHEN);
        assertThat(result.systemAction()).isEmpty();
    }

    @Test
    void executeShouldReturnSelectDishPublishedForSearchText() {
        final ExecutionResult result = service.execute(1L, "Искать");

        assertThat(result.chatStatus()).isEqualTo(ChatStatus.SELECT_DISH_PUBLISHED);
        assertThat(result.systemAction()).isEmpty();
    }

    @Test
    void executeShouldReturnRootSelectDishForUnknownText() {
        final ExecutionResult result = service.execute(1L, "что-то другое");

        assertThat(result.chatStatus()).isEqualTo(ChatStatus.ROOT_SELECT_DISH);
        assertThat(result.systemAction()).isEmpty();
    }

    @Test
    void executeShouldReturnRootSelectDishForEmptyText() {
        final ExecutionResult result = service.execute(1L, "");

        assertThat(result.chatStatus()).isEqualTo(ChatStatus.ROOT_SELECT_DISH);
        assertThat(result.systemAction()).isEmpty();
    }

    @Test
    void executeShouldNotDependOnUserId() {
        final ExecutionResult result1 = service.execute(1L, "По остроте");
        final ExecutionResult result2 = service.execute(999L, "По остроте");

        assertThat(result1.chatStatus()).isEqualTo(result2.chatStatus());
    }
}
