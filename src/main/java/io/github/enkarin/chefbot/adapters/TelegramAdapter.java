package io.github.enkarin.chefbot.adapters;

import io.github.enkarin.chefbot.controllers.TelegramController;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.dto.ModerationResultDto;
import io.github.enkarin.chefbot.dto.OperationResult;
import io.github.enkarin.chefbot.uicomponents.FormatedReplyKeyboardMarkup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
                    processingResponse(chatId, telegramController.processingNonCommandInput(userId, text));
                }
            }
        } else {
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            final String callbackData = callbackQuery.getData();
            if (callbackData.startsWith("A")) {
                final ModerationResultDto moderationResultDto = telegramController.approveModerationRequest(callbackData.substring(1));
                sendApproveResultToOwner(moderationResultDto);
            } else {
                send(callbackQuery.getMessage().getChatId(), telegramController.declineModerationRequest(callbackQuery.getFrom().getId(), callbackData.substring(1)));
            }
        }
    }

    private void processingResponse(final long chatId, final OperationResult operationResult) {
        operationResult.systemAction().ifPresent(action -> {
            if (action instanceof ModerationResultDto moderationResultDto) {
                sendDeclineResultToOwner(moderationResultDto);
            } else if (action instanceof ModerationDishDto moderationDishDto) {
                moderationDishDto.getOldModerationRequests().forEach(this::deleteOddRequestMessage);
                telegramController.addRequestMessages(moderationDishDto.getRequestId(),
                        sendModerationRequests(telegramController.findAvailableModeratorsId(moderationDishDto.getOwnerChatId()), moderationDishDto));
            } else if (action instanceof List<?> dishDtoList) {
                if (dishDtoList.isEmpty()) {
                    send(chatId, new BotAnswer("Подходящих блюд не найдено!"));
                } else {
                    dishDtoList.forEach(displayDishDto -> send(chatId, new BotAnswer(displayDishDto.toString())));
                }
            }
        });
        send(chatId, operationResult.botAnswer());
    }

    private void sendApproveResultToOwner(final ModerationResultDto moderationResultDto) {
        try {
            execute(defaultConfigurationMessage(moderationResultDto.ownerChat(),
                    "Блюдо ".concat(moderationResultDto.dishName()).concat(" прошло модерацию и успешно опубликовано!")));
            moderationResultDto.messageForRemove().forEach(this::deleteOddRequestMessage);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void sendDeclineResultToOwner(final ModerationResultDto moderationResultDto) {
        try {
            execute(defaultConfigurationMessage(moderationResultDto.ownerChat(), "Блюдо "
                    .concat(moderationResultDto.dishName())
                    .concat(" не прошло модерацию по причине:\n")
                    .concat(moderationResultDto.declineCause())));
            moderationResultDto.messageForRemove().forEach(this::deleteOddRequestMessage);
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
            botAnswer.userAnswerOptions().ifPresentOrElse(userAnswer -> {
                if (userAnswer.length != 0) {
                    sendMessage.setReplyMarkup(new FormatedReplyKeyboardMarkup(userAnswer));
                }
            }, () -> sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true)));
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
                        InlineKeyboardButton.builder()
                                .text("Одобрить заявку")
                                .callbackData("A" + moderationDishDto.getRequestId())
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("Отклонить заявку")
                                .callbackData("D" + moderationDishDto.getRequestId())
                                .build()))));
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
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(Long.toString(chatId));
        return sendMessage;
    }
}
