package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramAdapterTest extends TestBase {

    @Autowired
    private TelegramAdapter telegramAdapter;

    @Test
    void onUpdateReceivedForStartCommand() {
        telegramAdapter.onUpdateReceived(createTelegramCommand("/start"));

        assertThat(userRepository.existsById(CHAT_ID)).isTrue();
    }

    @Test
    void onUpdateReceivedForChangeModeratorStatusCommand() {
        userRepository.save(User.builder().chatId(CHAT_ID).chatStatus(ChatStatus.MAIN_MENU).build());
        telegramAdapter.onUpdateReceived(createTelegramCommand("/change_moderator_status"));

        assertThat(userRepository.findById(CHAT_ID).orElseThrow().isModerator()).isTrue();
    }

    private Update createTelegramCommand(final String text) {
        final Message message = new Message();
        message.setChat(new Chat(CHAT_ID, "test"));
        message.setText(text);
        message.setEntities(List.of(new MessageEntity("bot_command", 0, 0)));
        final Update update = new Update();
        update.setMessage(message);
        return update;
    }
}
