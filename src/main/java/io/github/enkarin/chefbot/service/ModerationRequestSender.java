package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.adapters.TelegramAdapter;
import io.github.enkarin.chefbot.dto.ModerationDishDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ModerationRequestSender {
    private final ModerationService moderationService;
    private final TelegramAdapter adapter;
    private final UserService userService;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    void sendFreshRequests() {
        sendRequests(moderationService.findAllFreshRequests());
    }

    @Scheduled(initialDelay = 1, fixedRate = 1, timeUnit = TimeUnit.DAYS)
    void resendOldRequests() {
        sendRequests(moderationService.findAllRequests());
    }

    private void sendRequests(final Set<ModerationDishDto> moderationDishDtoSet) {
        moderationDishDtoSet.forEach(moderationDishDto -> moderationService.addRequestMessages(moderationDishDto.getRequestId(),
                adapter.sendModerationRequests(userService.getAllModeratorsWithoutCurrentUser(moderationDishDto.getOwnerChatId()), moderationDishDto)));
    }
}
