package io.github.enkarin.chefbot.service.pipelines.addendum;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.service.ModerationService;
import io.github.enkarin.chefbot.service.pipelines.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ProcessingAddDishNeedPublishService implements ProcessingService {
    @Autowired
    private ModerationService moderationService;

    @Override
    public ChatStatus execute(final long userId, final String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "да" -> {
                moderationService.createModerationRequest(userId);
                yield ChatStatus.MAIN_MENU;
            }
            case "нет" -> ChatStatus.MAIN_MENU;
            default -> getCurrentStatus();
        };
    }

    @Override
    public BotAnswer getMessageForUser(final long userId) {
        return new BotAnswer("""
                Хотите опубликовать это блюдо?
                Когда оно пройдёт модерацию, то станет доступно всем пользователям.
                Блюдо останется доступно вам вне зависимости от результата модерации.
                """, UserAnswerOption.YES_OR_NO);
    }

    @Override
    public ChatStatus getCurrentStatus() {
        return ChatStatus.NEW_DISH_NEED_PUBLISH;
    }
}
