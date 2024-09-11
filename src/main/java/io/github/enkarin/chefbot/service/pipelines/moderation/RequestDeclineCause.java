package io.github.enkarin.chefbot.service.pipelines.moderation;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.ModerationService;
import io.github.enkarin.chefbot.service.UserService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestDeclineCause implements ProcessingService {
    private final ModerationService moderationService;
    private final UserService userService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        moderationService.declineRequest(userId, text);
        return new ExecutionResult(userService.getPreviousChatStatus(userId));
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
