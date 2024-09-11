package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.adapters.TelegramAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ModerationRequestSender {
    private final ModerationService moderationService;
    private final TelegramAdapter adapter;
    private final UserService userService;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    void resendOldRequests() {
        moderationService.findAllRequests().forEach(moderationDishDto -> moderationService.addRequestMessages(moderationDishDto.getRequestId(),
                adapter.sendModerationRequests(userService.getAllModeratorsWithoutCurrentUser(moderationDishDto.getOwnerChatId()), moderationDishDto)));
    }
}
