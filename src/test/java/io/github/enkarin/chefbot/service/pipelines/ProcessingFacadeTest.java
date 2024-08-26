package io.github.enkarin.chefbot.service.pipelines;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.github.enkarin.chefbot.enums.UserAnswerOption.YES_NO_OR_ANY;
import static io.github.enkarin.chefbot.enums.UserAnswerOption.YES_OR_NO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessingFacadeTest extends TestBase {
    @Autowired
    private ProcessingFacade processingFacade;

    @Autowired
    private SearchFilterRepository searchFilterRepository;

    @Test
    void execute() {
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username(USERNAME).chatStatus(ChatStatus.APPROVE_BACK_TO_MAIN_MENU).build());

        assertThat(processingFacade.execute(USER_ID, "Да").messageText()).isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithNewDishNeedPublish() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.NEW_DISH_NEED_PUBLISH);

        assertThat(processingFacade.execute(USER_ID, "aboba").messageText()).isEqualTo("Хотите опубликовать это блюдо, чтобы оно было доступно всем пользователям?");
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.NEW_DISH_NEED_PUBLISH);
    }

    @Test
    void goToStatus() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(processingFacade.goToStatus(USER_ID, ChatStatus.APPROVE_BACK_TO_MAIN_MENU)).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Вы хотите вернуться в главное меню? Весь прогресс текущей операции будет утерян.");
            assertThat(botAnswer.userAnswerOption()).isEqualTo(YES_OR_NO);
        });

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
    }

    @Test
    void undo() {
        userRepository.save(User.builder()
                .id(USER_ID)
                .chatId(CHAT_ID)
                .username(USERNAME)
                .chatStatus(ChatStatus.APPROVE_BACK_TO_MAIN_MENU)
                .previousChatStatus(ChatStatus.MAIN_MENU)
                .build());

        assertThat(processingFacade.undo(USER_ID).messageText()).isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @ParameterizedTest
    @MethodSource("provideStatusAndAnswer")
    void goToStatusShouldWork(final ChatStatus status, final String messageText, final UserAnswerOption userAnswerOption) {
        userRepository.save(User.builder()
                .id(USER_ID)
                .chatId(CHAT_ID)
                .chatStatus(ChatStatus.MAIN_MENU)
                .searchFilter(searchFilterRepository.save(new SearchFilter()))
                .build());
        initDishes();

        assertThat(processingFacade.goToStatus(USER_ID, status))
                .extracting(BotAnswer::messageText, BotAnswer::userAnswerOption)
                .containsOnly(messageText, userAnswerOption);
    }

    static Stream<Arguments> provideStatusAndAnswer() {
        return Stream.of(
                Arguments.of(ChatStatus.SELECT_DISH_PUBLISHED, "Включить блюда других пользователей при поиске?", YES_OR_NO),
                Arguments.of(ChatStatus.SELECT_DISH_SOUP, "Вы хотите суп?", YES_NO_OR_ANY),
                Arguments.of(ChatStatus.SELECT_DISH_SPICY, "Острое блюдо?", YES_NO_OR_ANY),
                Arguments.of(ChatStatus.SELECT_DISH_KITCHEN, "Выберите кухню мира:", UserAnswerOption.CUISINES),
                Arguments.of(ChatStatus.EXECUTE_SEARCH, "*fifth:*\n-fifthProduct\n\n*sixth:*\n-sixthProduct", UserAnswerOption.MORE_OR_STOP)
        );
    }

    @Test
    void goToStatusShouldWorkWithExecuteSearch() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.SELECT_DISH_SOUP);
        initDishes();

        processingFacade.execute(USER_ID, "да"); //select soup
        processingFacade.execute(USER_ID, "нет"); //select spicy
        processingFacade.execute(USER_ID, "Славянская"); //select cuisine
        final BotAnswer userMessage = processingFacade.execute(USER_ID, "да"); //select published

        assertThat(userMessage)
                .isNotNull()
                .extracting(BotAnswer::messageText)
                .isEqualTo("""
                        *third:*
                        -thirdProduct""");
    }

    @Test
    void goToStatusShouldThrowExceptionWhenDishNotFound() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.SELECT_DISH_SOUP);

        processingFacade.execute(USER_ID, "да"); //select soup
        processingFacade.execute(USER_ID, "нет"); //select spicy
        processingFacade.execute(USER_ID, "Славянская"); //select cuisine
        assertThatThrownBy(() -> processingFacade.execute(USER_ID, "да")) //select published
                .isInstanceOf(DishesNotFoundException.class)
                .hasMessage("Подходящие блюда не найдены. Вы возвращены в главное меню.");
    }

    @Test
    void goToStatusShouldWorkWithAddDish() {
        createUser(ChatStatus.NEW_DISH_NAME);

        final String dishName = "Кимчи суп";
        processingFacade.execute(USER_ID, dishName); //enter dish name
        processingFacade.execute(USER_ID, "Да"); //enter is soup
        processingFacade.execute(USER_ID, "Да"); //enter is spicy
        processingFacade.execute(USER_ID, "Азиатская"); //enter cuisine
        processingFacade.execute(USER_ID, "кимчи, свинина, репчатый лук, перцовая паста кочудян, тофу"); //enter foodstuff
        processingFacade.execute(USER_ID, "нет"); //enter is need publish

        assertThat(dishRepository.findAll())
                .hasSize(1)
                .first()
                .satisfies(d -> {
                    assertThat(d.getDishName())
                            .isEqualTo(dishName);
                    assertThat(d.isSoup())
                            .isTrue();
                    assertThat(d.isSpicy())
                            .isTrue();
                    assertThat(d.getCuisine())
                            .isEqualTo(WorldCuisine.ASIA);
                });
        assertThat(productRepository.findAll())
                .hasSize(5)
                .extracting(Product::getProductName)
                .containsOnly("Кимчи", "Свинина", "Репчатый лук", "Перцовая паста кочудян", "Тофу");
    }
}