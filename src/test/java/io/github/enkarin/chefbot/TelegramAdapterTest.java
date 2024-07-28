package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.util.TestBase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class TelegramAdapterTest extends TestBase {

    @Autowired
    private TelegramAdapter telegramAdapter;

    @Test
    void onUpdateReceivedForStartCommand() {
        telegramAdapter.onUpdateReceived(createTelegramCommand(CHAT_ID, "/start"));

        assertThat(userRepository.existsById(CHAT_ID)).isTrue();
    }

    @Test
    void onUpdateReceivedForChangeModeratorStatusCommand() {
        userRepository.save(User.builder().chatId(CHAT_ID).build());
        telegramAdapter.onUpdateReceived(createTelegramCommand(CHAT_ID, "/change_moderator_status"));

        assertThat(userRepository.findById(CHAT_ID).orElseThrow().isModerator()).isTrue();
    }

    @Test
    @SneakyThrows
    void onUpdateReceivedForUnexpectedCommand() {
        assertThatCode(() -> telegramAdapter.onUpdateReceived(createTelegramCommand(CHAT_ID, "/aboba")))
                .doesNotThrowAnyException();
    }
}
