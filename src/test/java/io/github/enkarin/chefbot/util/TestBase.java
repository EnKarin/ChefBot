package io.github.enkarin.chefbot.util;

import io.github.enkarin.chefbot.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
public class TestBase {

    protected static final Long CHAT_ID = Long.MAX_VALUE;

    @MockBean
    protected TelegramBotsApi telegramBotApi;
    @Autowired
    protected UserRepository userRepository;

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }
}
