package io.github.enkarin.chefbot;

import io.github.enkarin.chefbot.service.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public final class TelegramAdapter extends TelegramLongPollingBot {
    @Getter
    private final String botUsername;
    @Getter
    private final String botToken;
    private final UserService userService;

    @SuppressWarnings("PMD.CallSuperInConstructor")
    public TelegramAdapter(final TelegramBotsApi telegramBotsApi,
                           @Value("${telegram-bot.name}") final String botUsername,
                           @Value("${telegram-bot.token}") final String botToken,
                           final UserService userService) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userService = userService;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(final Update update) {
        final Message message = update.getMessage();
        final long chatId = message.getChatId();
        if (message.isCommand()) {
            switch (message.getText()) {
                case "/start" -> {
                    userService.findOrSaveUser(chatId);
                    send(chatId, "Приветствую! Здесь вы можете найти блюдо по вашим предпочтениям " +
                            "и поделиться своими рецептами с другими пользователями");
                }
                case "/change_moderator_status" -> userService.changeModeratorStatus(chatId);
                default -> send(chatId, "Указанной команды не существует");
            }
        }
    }

    private void send(final long chatId, final String message) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(Long.toString(chatId));
        try {
            execute(sendMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
