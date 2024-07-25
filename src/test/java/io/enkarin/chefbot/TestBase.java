package io.enkarin.chefbot;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SpringBootTest
@ActiveProfiles("test")
public class TestBase {

    @MockBean
    protected TelegramBotsApi telegramBotAdapter;
}
