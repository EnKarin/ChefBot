package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.adapters.TelegramAdapter;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModerationRequestSenderTest extends ModerationTest {
    @MockBean
    private TelegramAdapter adapter;

    @Autowired
    private ModerationRequestSender moderationRequestSender;

    @Test
    void sendFreshRequests() {
        when(adapter.sendModerationRequests(eq(Set.of(CHAT_ID - 1)), any())).thenReturn(Set.of(new ModerationRequestMessageDto(1000, 1111)));

        moderationRequestSender.sendFreshRequests();

        verify(adapter, times(2)).sendModerationRequests(eq(Set.of(CHAT_ID - 1)), any());
        assertThat(moderationRequestRepository.findAll()).noneMatch(ModerationRequest::isFresh);
        assertThat(moderationRequestMessageRepository.findAll()).extracting(ModerationRequestMessage::getChatId).filteredOn(chatId -> chatId == 1111).hasSize(2);
    }

    @Test
    void resendOldRequests() {
    }
}