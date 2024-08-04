package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcessingFacade {
    private final Map<ChatStatus, ProcessingService> processingServiceMap;
    private final UserService userService;

    public ProcessingFacade(final List<ProcessingService> processingServiceList, final UserService userService) {
        processingServiceMap = processingServiceList.stream().collect(Collectors.toMap(ProcessingService::getCurrentStatus, Function.identity()));
        this.userService = userService;
    }

    public BotAnswer execute(final long userId, final String text) {
        return goToStatus(userId, processingServiceMap.get(userService.getChatStatus(userId)).execute(userId, text));
    }

    public BotAnswer goToStatus(final long userId, final ChatStatus newChatStatus) {
        userService.switchToNewStatus(userId, newChatStatus);
        return processingServiceMap.get(newChatStatus).getMessageForUser();
    }

    public BotAnswer undo(final long userId) {
        if (userService.canUndo(userId)) {
            return processingServiceMap.get(userService.backToPreviousStatus(userId)).getMessageForUser();
        } else {
            return new BotAnswer("Отменить действие можно лишь один раз подряд");
        }
    }
}
