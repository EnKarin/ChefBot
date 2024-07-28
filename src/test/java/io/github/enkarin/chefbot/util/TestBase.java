package io.github.enkarin.chefbot.util;

import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.UserRepository;
import io.github.enkarin.chefbot.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
public class TestBase {

    protected static final Long CHAT_ID = Long.MAX_VALUE;

    @MockBean
    protected TelegramBotsApi telegramBotApi;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected DishRepository dishRepository;


    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    protected Update createTelegramCommand(final long chatId, final String text) {
        final Message message = new Message();
        message.setChat(new Chat(chatId, "test"));
        message.setText(text);
        message.setEntities(List.of(new MessageEntity("bot_command", 0, 0)));
        final Update update = new Update();
        update.setMessage(message);
        return update;
    }
}
