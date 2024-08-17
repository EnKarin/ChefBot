package io.github.enkarin.chefbot.adapters;

import io.github.enkarin.chefbot.controllers.TelegramController;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.uicomponents.FormatedReplyKeyboardMarkup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public final class TelegramAdapter extends TelegramLongPollingBot {
    @Getter
    private final String botUsername;
    @Getter
    private final String botToken;
    private final TelegramController telegramController;

    public TelegramAdapter(final TelegramBotsApi telegramBotsApi,
                           @Value("${telegram-bot.name}") final String botUsername,
                           @Value("${telegram-bot.token}") final String botToken,
                           final TelegramController telegramController) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.telegramController = telegramController;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(final Update update) {
        final Message message = update.getMessage();
        final long chatId = message.getChatId();
        final String text = message.getText();
        final long userId = message.getFrom().getId();
        final String username = message.getFrom().getUserName();
        if ("/start".equals(text)) {
            send(chatId, telegramController.executeStartCommand(userId, chatId, username));
        } else {
            if (message.isCommand()) {
                send(chatId, telegramController.executeWorkerCommand(userId, text));
            } else {
                send(chatId, telegramController.processingNonCommandInput(userId, text));
            }
        }
    }

    private void send(final long chatId, final BotAnswer botAnswer) {
        try {
            final SendMessage sendMessage = defaultConfigurationMessage(chatId, botAnswer.messageText());
            if (botAnswer.userAnswerOption() != UserAnswerOption.DEFAULT) {
                sendMessage.setReplyMarkup(botAnswer.userAnswerOption() == UserAnswerOption.NONE
                        ? new ReplyKeyboardRemove()
                        : new FormatedReplyKeyboardMarkup(botAnswer.userAnswerOption()));
            }
            execute(sendMessage);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public Set<ModerationRequestMessageDto> sendModerationRequests(final Set<Long> chats, final ModerationDishDto moderationDishDto) {
        final Set<ModerationRequestMessageDto> requestMessageDtoSet = new HashSet<>();
        for (final long chatId : chats) {
            try {
                final SendMessage moderationMessage = defaultConfigurationMessage(chatId, moderationDishDto.toString());
                moderationMessage.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(new InlineKeyboardButton("Одобрить заявку №" + moderationDishDto.getRequestId()),
                        new InlineKeyboardButton("Отклонить заявку №" + moderationDishDto.getRequestId())))));
                requestMessageDtoSet.add(new ModerationRequestMessageDto(execute(moderationMessage).getMessageId(), chatId));
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return requestMessageDtoSet;
    }

    private SendMessage defaultConfigurationMessage(final long chatId, final String text) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(Long.toString(chatId));
        return sendMessage;
    }
}
