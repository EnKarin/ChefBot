package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.dto.ExecutionResult;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.service.ModerationService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessingAddDishNeedPublishService implements ProcessingService {
    private final ModerationService moderationService;

    @Override
    public ExecutionResult execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> new ExecutionResult(ChatStatus.MAIN_MENU, moderationService.createModerationRequest(userId));
            case "нет" -> new ExecutionResult(ChatStatus.MAIN_MENU);
            default -> new ExecutionResult(getCurrentStatus());
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("""
                Хотите опубликовать это блюдо?
                Когда оно пройдёт модерацию, то станет доступно всем пользователям.
                Блюдо останется доступно вам вне зависимости от результата модерации.
                """, StandardUserAnswerOption.YES_OR_NO);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_NEED_PUBLISH;
    }
}
