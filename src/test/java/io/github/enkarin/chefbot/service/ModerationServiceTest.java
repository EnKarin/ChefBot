package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.mappers.ModerationRequestMessageEntityDtoMapper;
import io.github.enkarin.chefbot.repository.ModerationRequestMessageRepository;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ModerationServiceTest extends TestBase {
    @Autowired
    private ModerationService moderationService;

    @Autowired
    private DishService dishService;

    @Autowired
    private ModerationRequestRepository moderationRequestRepository;

    @Autowired
    private ModerationRequestMessageRepository moderationRequestMessageRepository;

    @Autowired
    private ModerationRequestMessageEntityDtoMapper mapper;

    private final long[] moderationRequestsId = new long[4];

    @BeforeEach
    void init() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        final User user = userService.findUser(USER_ID);
        moderationRequestsId[0] = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(false)
                .moderationRequestMessages(moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(1).chatId(10).build(),
                                ModerationRequestMessage.builder().messageId(1).chatId(11).build())))
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("firstDish").build()))
                .build()).getId();
        moderationRequestsId[1] =  moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(false)
                .moderationRequestMessages(moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(2).chatId(20).build(),
                        ModerationRequestMessage.builder().messageId(2).chatId(22).build())))
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("secondDish").build()))
                .build()).getId();
        moderationRequestsId[2] = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(true)
                .moderationRequestMessages(moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(3).chatId(30).build(),
                        ModerationRequestMessage.builder().messageId(3).chatId(33).build())))
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("thirdDish").build()))
                .build()).getId();
        moderationRequestsId[3] = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(true)
                .moderationRequestMessages(moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(4).chatId(40).build(),
                        ModerationRequestMessage.builder().messageId(4).chatId(44).build())))
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("fourthDish").build()))
                .build()).getId();
    }

    @AfterEach
    void clean() {
        moderationRequestRepository.deleteAll();
    }

    @Test
    void createModerationRequest() {
        final Dish dish = dishRepository.save(Dish.builder().owner(userService.findUser(USER_ID)).dishName("newDish").build());

        moderationService.createModerationRequest(dish);

        assertThat(moderationRequestRepository.findAll()).extracting(ModerationRequest::getModerationDish).extracting(Dish::getDishName).contains("newDish");
    }

    @Test
    @Transactional
    void addRequestMessages() {
        final Set<ModerationRequestMessageDto> messageDtoSet = moderationRequestMessageRepository
                .saveAll(List.of(ModerationRequestMessage.builder().messageId(13).chatId(130).build(), ModerationRequestMessage.builder().messageId(13).chatId(133).build()))
                .stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toSet());

        moderationService.addRequestMessages(moderationRequestsId[0], messageDtoSet);

        assertThat(moderationRequestRepository.findById(moderationRequestsId[0]).orElseThrow().getModerationRequestMessages())
                .extracting(ModerationRequestMessage::getChatId)
                .contains(130L, 133L, 10L, 11L);
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
    void approveRequest() {
        assertThat(moderationService.approveRequest(moderationRequestsId[1])).satisfies(moderationResultDto -> {
            assertThat(moderationResultDto.approve()).isTrue();
            assertThat(moderationResultDto.name()).isEqualTo("secondDish");
            assertThat(moderationResultDto.ownerChat()).isEqualTo(CHAT_ID);
            assertThat(moderationResultDto.messageForRemove()).extracting(ModerationRequestMessageDto::getChatId).contains(20L, 22L);
        });
        assertThat(moderationRequestRepository.existsById(moderationRequestsId[1])).isFalse();
    }

    @Test
    void declineRequest() {
        assertThat(moderationService.approveRequest(moderationRequestsId[2])).satisfies(moderationResultDto -> {
            assertThat(moderationResultDto.approve()).isFalse();
            assertThat(moderationResultDto.name()).isEqualTo("thirdDish");
            assertThat(moderationResultDto.ownerChat()).isEqualTo(CHAT_ID);
            assertThat(moderationResultDto.messageForRemove()).extracting(ModerationRequestMessageDto::getChatId).contains(30L, 33L);
        });
        assertThat(moderationRequestRepository.existsById(moderationRequestsId[2])).isFalse();
    }
}