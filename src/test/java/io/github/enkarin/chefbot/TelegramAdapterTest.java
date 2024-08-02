package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.service.ProcessingFacade;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramAdapterTest extends TestBase {
    @MockBean
    private ProcessingFacade processingFacade;

    @Autowired
    private TelegramAdapter telegramAdapter;

    @Test
    void onUpdateReceivedStartCommand() {
        telegramAdapter.onUpdateReceived(createTelegramCommand("/start"));

        assertThat(userRepository.existsById(USER_ID)).isTrue();
    }

    @Test
    void onUpdateReceivedChangeModeratorStatusCommand() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.SELECT_DISH_PRICE).build());
        telegramAdapter.onUpdateReceived(createTelegramCommand("/back_to_main_menu"));

        assertThat(userRepository.findById(USER_ID).orElseThrow().getChatStatus()).isEqualTo(ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
    }

    @Test
    void onUpdateReceivedNotCommandInput() {
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.NEW_DISH_SPICY).build());
        final Message message = new Message();
        message.setText("test text");
        message.setChat(new Chat(USER_ID, "test chat"));
        final org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(USER_ID);
        user.setUserName(USERNAME);
        message.setFrom(user);
        final Update update = new Update();
        update.setMessage(message);
        Mockito.when(processingFacade.execute(USER_ID, ChatStatus.NEW_DISH_SPICY, "test text")).thenReturn(new BotAnswer("Dish is spicy?", UserAnswerOption.YES_OR_NO));

        telegramAdapter.onUpdateReceived(update);

        Mockito.verify(processingFacade).execute(USER_ID, ChatStatus.NEW_DISH_SPICY, "test text");
    }

    private Update createTelegramCommand(final String text) {
        final Message message = new Message();
        message.setChat(new Chat(USER_ID, "test"));
        message.setText(text);
        message.setEntities(List.of(new MessageEntity("bot_command", 0, 0)));
        final org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(USER_ID);
        user.setUserName(USERNAME);
        message.setFrom(user);
        final Update update = new Update();
        update.setMessage(message);
        return update;
    }
}
