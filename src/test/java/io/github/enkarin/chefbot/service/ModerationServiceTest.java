package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import io.github.enkarin.chefbot.mappers.ModerationRequestMessageEntityDtoMapper;
import io.github.enkarin.chefbot.util.ModerationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        moderationInit();
    }

    @Test
    void createModerationRequest() {
        moderationRequestRepository.deleteAll();
        dishService.initDishName(USER_ID, "newDish");

        moderationService.createModerationRequest(USER_ID);

        assertThat(moderationRequestRepository.findAll()).hasSize(1).extracting(ModerationRequest::getModerationDish).extracting(Dish::getDishName).contains("newDish");
    }

    @Test
    void createModerationRequestWithShowQuantityOfProduct() {
        moderationRequestRepository.deleteAll();
        dishService.initDishName(USER_ID, "newDish");
        dishService.putAllDishFoodstuff(USER_ID, Map.of("Egg", "2", "Milk", "200 ml", "Salt", "2 gram"));

        assertThat(moderationService.createModerationRequest(USER_ID).getProducts()).containsOnly("Egg: 2", "Milk: 200 ml", "Salt: 2 gram");
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
    }

    @Test
    void findAllRequests() {
        assertThat(moderationService.findAllRequests())
                .extracting(ModerationDishDto::getName)
                .containsOnly("firstDish", "secondDish", "thirdDish", "fourthDish", "fifthDish");
    }

    @Test
    void approveRequest() {
        assertThat(moderationService.approveRequest(moderationRequestsId[1])).satisfies(moderationResultDto -> {
            assertThat(moderationResultDto.isApprove()).isTrue();
            assertThat(moderationResultDto.dishName()).isEqualTo("secondDish");
            assertThat(moderationResultDto.ownerChat()).isEqualTo(CHAT_ID);
            assertThat(moderationResultDto.messageForRemove()).extracting(ModerationRequestMessageDto::chatId).containsOnly(20L, 22L);
        });
        assertThat(moderationRequestRepository.existsById(moderationRequestsId[1])).isFalse();
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.getDishName()).isEqualTo("secondDish");
            assertThat(dish.isPublished()).isTrue();
        });
    }

    @Test
    void declineRequest() {
        moderationService.startModerate(USER_ID, moderationRequestsId[2]);

        moderationService.declineRequest(USER_ID, "Bad request");

        assertThat(userService.findUser(USER_ID).getModerableDish()).isNull();
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.getDishName()).isEqualTo("thirdDish");
            assertThat(dish.isPublished()).isFalse();
        });
    }

    @Test
    void startModerate() {
        moderationService.startModerate(USER_ID, moderationRequestsId[3]);

        assertThat(jdbcTemplate.queryForObject("select dish_name from moderation_request inner join t_dish on moderation_dish=dish_id where mr_id=?",
                String.class,
                moderationRequestsId[3])).isEqualTo("fourthDish");
    }

    @Test
    void createRepeatedModerationRequest() {
        moderationRequestRepository.deleteAll();
        dishService.initDishName(USER_ID, "newDish");
        final long moderationRequestId = moderationService.createModerationRequest(USER_ID).getRequestId();
        dishService.putDishRecipe(USER_ID, "Recipe");
        moderationService.addRequestMessages(moderationRequestId, Set.of(new ModerationRequestMessageDto(1, CHAT_ID - 1)));

        assertThat(moderationService.createModerationRequest(USER_ID).getOldModerationRequests())
                .extracting(ModerationRequestMessageDto::chatId)
                .containsOnly(CHAT_ID - 1);
        assertThat(moderationRequestRepository.findAll())
                .hasSize(1)
                .extracting(ModerationRequest::getModerationDish)
                .extracting(Dish::getDishName)
                .contains("newDish");
        assertThat(moderationRequestMessageRepository.count()).isZero();
    }
}
