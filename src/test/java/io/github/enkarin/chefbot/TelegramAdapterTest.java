package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.adapters.TelegramAdapter;
import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.ModerationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramAdapterTest extends ModerationTest {
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
        userRepository.save(User.builder().id(USER_ID).chatStatus(ChatStatus.APPROVE_BACK_TO_MAIN_MENU).build());
        final Message message = new Message();
        message.setText("Да");
        message.setChat(new Chat(USER_ID, "test chat"));
        final org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(USER_ID);
        user.setUserName(USERNAME);
        message.setFrom(user);
        final Update update = new Update();
        update.setMessage(message);

        telegramAdapter.onUpdateReceived(update);

        assertThat(userRepository.findById(USER_ID).orElseThrow().getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
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

    @Test
    void sendModerationRequests() {
        assertThat(telegramAdapter.sendModerationRequests(Set.of(10L, 20L), ModerationDishDto.builder().requestId(666).build())).isNotNull();
    }

    @Test
    void approveModerationRequest() {
        moderationInit();

        telegramAdapter.onUpdateReceived(createUpdateWithCallbackQuery("Одобрить запрос №" + moderationRequestsId[0]));

        assertThat(moderationRequestRepository.existsById(moderationRequestsId[0])).isFalse();

        moderationClean();
    }

    @Test
    void declineModerationRequest() {
        moderationInit();

        telegramAdapter.onUpdateReceived(createUpdateWithCallbackQuery("Отклонить запрос №" + moderationRequestsId[1]));

        assertThat(moderationRequestRepository.existsById(moderationRequestsId[1])).isFalse();

        moderationClean();
    }

    private Update createUpdateWithCallbackQuery(final String data) {
        final Update update = new Update();
        final CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(data);
        update.setCallbackQuery(callbackQuery);
        return update;
    }
}
