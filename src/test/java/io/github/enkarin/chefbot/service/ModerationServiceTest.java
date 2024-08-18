package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import io.github.enkarin.chefbot.mappers.ModerationRequestMessageEntityDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ModerationServiceTest extends ModerationTest {
    @Autowired
    private ModerationService moderationService;

    @Autowired
    private ModerationRequestMessageEntityDtoMapper mapper;

    @Autowired
    private DishService dishService;

    @Test
    void createModerationRequest() {
        dishService.initDishName(USER_ID, "newDish");

        moderationService.createModerationRequest(USER_ID);

        assertThat(moderationRequestRepository.findAll()).extracting(ModerationRequest::getModerationDish).extracting(Dish::getDishName).contains("newDish");
    }

    @Test
    void addRequestMessages() {
        final Set<ModerationRequestMessageDto> messageDtoSet = moderationRequestMessageRepository
                .saveAll(List.of(ModerationRequestMessage.builder().messageId(13).chatId(130).build(), ModerationRequestMessage.builder().messageId(13).chatId(133).build()))
                .stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toSet());

        moderationService.addRequestMessages(moderationRequestsId[0], messageDtoSet);

        assertThat(moderationRequestMessageRepository.findAll()).extracting(ModerationRequestMessage::getChatId).contains(130L, 133L, 10L, 11L);
        assertThat(moderationService.declineRequest(moderationRequestsId[0]).messageForRemove()).extracting(ModerationRequestMessageDto::chatId).contains(130L, 133L, 10L, 11L);
    }

    @Test
    void findAllFreshRequests() {
        assertThat(moderationService.findAllFreshRequests()).extracting(ModerationDishDto::getName).contains("thirdDish", "fourthDish");
    }

    @Test
    void findAllFreshRequestsMustBeNonFreshAfterCall() {
        moderationService.findAllFreshRequests();

        assertThat(moderationRequestRepository.findAll()).noneMatch(ModerationRequest::isFresh);
    }

    @Test
    void findAllRequests() {
        assertThat(moderationService.findAllRequests()).extracting(ModerationDishDto::getName).contains("firstDish", "secondDish", "thirdDish", "fourthDish");
    }

    @Test
    void findAllRequestsMustBeNonFreshAfterCall() {
        moderationService.findAllRequests();

        assertThat(moderationRequestRepository.findAll()).noneMatch(ModerationRequest::isFresh);
    }

    @Test
    void approveRequest() {
        assertThat(moderationService.approveRequest(moderationRequestsId[1])).satisfies(moderationResultDto -> {
            assertThat(moderationResultDto.approve()).isTrue();
            assertThat(moderationResultDto.dishName()).isEqualTo("secondDish");
            assertThat(moderationResultDto.ownerChat()).isEqualTo(CHAT_ID);
            assertThat(moderationResultDto.messageForRemove()).extracting(ModerationRequestMessageDto::chatId).contains(20L, 22L);
        });
        assertThat(moderationRequestRepository.existsById(moderationRequestsId[1])).isFalse();
    }

    @Test
    void declineRequest() {
        assertThat(moderationService.declineRequest(moderationRequestsId[2])).satisfies(moderationResultDto -> {
            assertThat(moderationResultDto.approve()).isFalse();
            assertThat(moderationResultDto.dishName()).isEqualTo("thirdDish");
            assertThat(moderationResultDto.ownerChat()).isEqualTo(CHAT_ID);
            assertThat(moderationResultDto.messageForRemove()).extracting(ModerationRequestMessageDto::chatId).contains(30L, 33L);
        });
        assertThat(moderationRequestRepository.existsById(moderationRequestsId[2])).isFalse();
    }
}
