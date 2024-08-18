package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.adapters.TelegramAdapter;
import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.ModerationRequestMessageRepository;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.AfterEach;
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

class TelegramAdapterTest extends TestBase {
    @Autowired
    private TelegramAdapter telegramAdapter;

    @Autowired
    private ModerationRequestRepository moderationRequestRepository;

    @Autowired
    private ModerationRequestMessageRepository moderationRequestMessageRepository;

    private final long[] moderationRequestsId = new long[4];

    @AfterEach
    void clean() {
        moderationRequestRepository.deleteAll();
    }

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
    }

    @Test
    void declineModerationRequest() {
        moderationInit();

        telegramAdapter.onUpdateReceived(createUpdateWithCallbackQuery("Отклонить запрос №" + moderationRequestsId[1]));

        assertThat(moderationRequestRepository.existsById(moderationRequestsId[1])).isFalse();
    }

    private Update createUpdateWithCallbackQuery(final String data) {
        final Update update = new Update();
        final CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(data);
        update.setCallbackQuery(callbackQuery);
        return update;
    }

    private void moderationInit() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        final User user = userRepository.save(User.builder().moderator(true).id(USER_ID - 1).chatId(CHAT_ID - 1).username(USERNAME + 1).build());
        final ModerationRequest firstModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(false)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("firstDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(1).chatId(10).currentModerationRequest(firstModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(1).chatId(11).currentModerationRequest(firstModerationRequest).build()));
        moderationRequestsId[0] = firstModerationRequest.getId();
        final ModerationRequest secondModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(false)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("secondDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(2).chatId(20).currentModerationRequest(secondModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(2).chatId(22).currentModerationRequest(secondModerationRequest).build()));
        moderationRequestsId[1] = secondModerationRequest.getId();
        final ModerationRequest thirdModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(true)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("thirdDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(3).chatId(30).currentModerationRequest(thirdModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(3).chatId(33).currentModerationRequest(thirdModerationRequest).build()));
        moderationRequestsId[2] = thirdModerationRequest.getId();
        final ModerationRequest fourthModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(true)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("fourthDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(4).chatId(40).currentModerationRequest(fourthModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(4).chatId(44).currentModerationRequest(fourthModerationRequest).build()));
        moderationRequestsId[3] = fourthModerationRequest.getId();
    }
}
