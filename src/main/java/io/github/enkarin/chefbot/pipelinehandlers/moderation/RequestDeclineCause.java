package io.github.enkarin.chefbot.pipelinehandlers.moderation;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.NonCommandInputHandler;
import io.github.enkarin.chefbot.service.ModerationService;
import io.github.enkarin.chefbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestDeclineCause implements NonCommandInputHandler {
    private final ModerationService moderationService;
    private final UserService userService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return new ExecutionResult(userService.getPreviousChatStatus(userId), moderationService.declineRequest(userId, text));
    }

    @Override
    public BotAnswer getMessageForUser(long userId) {
        return BotAnswer.createBotAnswerWithoutKeyboard("Введите причину отклонения заявки");
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.WRITE_DECLINE_MODERATION_REQUEST;
    }
}
