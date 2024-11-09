package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class EnrichingRecipesCommandHandlerTest extends TestBase {
    @Autowired
    private EnrichingRecipesCommandHandler handler;

    @Test
    void correctExecute() {
        createUser(ChatStatus.MAIN_MENU);
        initDishes();

        assertThat(handler.execute(USER_ID)).satisfies(botAnswer -> {
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("fifth", "sixth");
            assertThat(botAnswer.messageText()).isEqualTo("Выберете добавленное вами ранее блюдо для добавления рецепта");
        });
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.ENRICHING_RECIPES);
    }

    @Test
    void executeWithNotFoundDishWithoutRecipes() {
        createUser(ChatStatus.MAIN_MENU);

        assertThat(handler.execute(USER_ID)).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("У вас нет блюд без рецептов. Вы возвращены в главное меню");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).isEmpty();
        });
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }
}
