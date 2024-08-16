package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.enkarin.chefbot.enums.UserAnswerOption.YES_OR_NO;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessingFacadeTest extends TestBase {
    @Autowired
    private ProcessingFacade processingFacade;

    @Test
    void execute() {
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username(USERNAME).chatStatus(ChatStatus.APPROVE_BACK_TO_MAIN_MENU).build());

        assertThat(processingFacade.execute(USER_ID, "Да").messageText()).isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
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
}
