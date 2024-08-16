package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.RequestMessageInfoDto;

import java.util.Set;

public interface MessageAdapter {
    Set<RequestMessageInfoDto> sendModerationRequests(Set<Long> chats, ModerationDishDto moderationDishDto);
}
