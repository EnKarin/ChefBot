package io.github.enkarin.chefbot.adapters;

import io.github.enkarin.chefbot.controllers.TelegramController;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.dto.ModerationResultDto;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.uicomponents.FormatedReplyKeyboardMarkup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public final class TelegramAdapter extends TelegramLongPollingBot {
    @Getter
    private final String botUsername;
    @Getter
    private final String botToken;
    private final String approvePrefix;
    private final String declinePrefix;
    private final TelegramController telegramController;

    public TelegramAdapter(final TelegramBotsApi telegramBotsApi,
                           @Value("${telegram-bot.name}") final String botUsername,
                           @Value("${telegram-bot.token}") final String botToken,
                           @Value("${adapter.prefix.approve}") final String approvePrefix,
                           @Value("${adapter.prefix.decline}") final String declinePrefix,
                           final TelegramController telegramController) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.telegramController = telegramController;
        this.approvePrefix = approvePrefix;
        this.declinePrefix = declinePrefix;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(final Update update) {
        final Message message = update.getMessage();
        if (nonNull(message)) {
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
        } else {
            final String callbackData = update.getCallbackQuery().getData();
            final ModerationResultDto moderationResultDto = callbackData.startsWith(approvePrefix)
                    ? telegramController.approveModerationRequest(callbackData)
                    : telegramController.declineModerationRequest(callbackData);
            if (moderationResultDto.approve()) {
                sendApproveResultToOwner(moderationResultDto.ownerChat(), moderationResultDto.dishName());
            } else {
                sendDeclineResultToOwner(moderationResultDto.ownerChat(), moderationResultDto.dishName());
            }
            moderationResultDto.messageForRemove().forEach(this::deleteOddRequestMessage);
        }
    }

    private void sendApproveResultToOwner(final long chatId, final String dishName) {
        try {
            execute(defaultConfigurationMessage(chatId, "Блюдо ".concat(dishName).concat(" прошло модерацию и успешно опубликовано!")));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void sendDeclineResultToOwner(final long chatId, final String dishName) {
        try {
            execute(defaultConfigurationMessage(chatId, "Блюдо ".concat(dishName).concat(" не прошло модерацию")));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void deleteOddRequestMessage(final ModerationRequestMessageDto messageDto) {
        try {
            execute(new DeleteMessage(Long.toString(messageDto.chatId()), messageDto.messageId()));
        } catch (Exception e) {
            log.error(e.toString());
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
                moderationMessage.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(
                        new InlineKeyboardButton(approvePrefix + " заявку №" + moderationDishDto.getRequestId()),
                        new InlineKeyboardButton(declinePrefix + " заявку №" + moderationDishDto.getRequestId())))));
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
