package io.github.enkarin.chefbot.pipelineHandlers;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.dto.OperationResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcessingFacade {
    private final Map<ChatStatus, NonCommandInputHandler> processingServiceMap;
    private final UserService userService;

    public ProcessingFacade(final List<NonCommandInputHandler> nonCommandInputHandlerList, final UserService userService) {
        processingServiceMap = nonCommandInputHandlerList.stream().collect(Collectors.toMap(NonCommandInputHandler::getCurrentStatus, Function.identity()));
        this.userService = userService;
    }

    public OperationResult execute(final long userId, final String text) {
        final ExecutionResult executionResult = processingServiceMap.get(userService.getChatStatus(userId)).execute(userId, text);
        return new OperationResult(goToStatus(userId, executionResult.chatStatus()), executionResult.systemAction());
    }

    public BotAnswer goToStatus(final long userId, final ChatStatus newChatStatus) {
        userService.switchToNewStatus(userId, newChatStatus);
        return processingServiceMap.get(newChatStatus).getMessageForUser(userId);
    }

    public BotAnswer undo(final long userId) {
        if (userService.canUndo(userId)) {
            return processingServiceMap.get(userService.backToPreviousStatus(userId)).getMessageForUser(userId);
        } else {
            return new BotAnswer("Отменить действие можно лишь один раз подряд");
        }
    }
}
