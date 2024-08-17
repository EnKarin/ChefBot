package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;

import java.util.Set;

public interface MessageAdapter {
    Set<ModerationRequestMessageDto> sendModerationRequests(Set<Long> chats, ModerationDishDto moderationDishDto);
}
