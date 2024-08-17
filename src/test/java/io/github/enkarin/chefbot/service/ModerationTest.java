package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.repository.ModerationRequestMessageRepository;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class ModerationTest extends TestBase {
    @Autowired
    protected ModerationRequestRepository moderationRequestRepository;

    @Autowired
    protected ModerationRequestMessageRepository moderationRequestMessageRepository;

    protected final long[] moderationRequestsId = new long[4];

    @BeforeEach
    void init() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userRepository.save(User.builder().moderator(true).id(USER_ID - 1).chatId(CHAT_ID - 1).username(USERNAME + 1).build());
        final User user = userService.findUser(USER_ID);
        final ModerationRequest firstModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(false)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("firstDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(1).chatId(10).currentModerationRequest(firstModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(1).chatId(11).currentModerationRequest(firstModerationRequest).build()));
        moderationRequestsId[0] = firstModerationRequest.getId();
        final ModerationRequest secondModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(false)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("secondDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(2).chatId(20).currentModerationRequest(secondModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(2).chatId(22).currentModerationRequest(secondModerationRequest).build()));
        moderationRequestsId[1] = secondModerationRequest.getId();
        final ModerationRequest thirdModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(true)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("thirdDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(3).chatId(30).currentModerationRequest(thirdModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(3).chatId(33).currentModerationRequest(thirdModerationRequest).build()));
        moderationRequestsId[2] = thirdModerationRequest.getId();
        final ModerationRequest fourthModerationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .fresh(true)
                .moderationDish(dishRepository.save(Dish.builder().owner(user).dishName("fourthDish").build()))
                .build());
        moderationRequestMessageRepository.saveAll(List.of(ModerationRequestMessage.builder().messageId(4).chatId(40).currentModerationRequest(fourthModerationRequest).build(),
                ModerationRequestMessage.builder().messageId(4).chatId(44).currentModerationRequest(fourthModerationRequest).build()));
        moderationRequestsId[3] = fourthModerationRequest.getId();
    }

    @AfterEach
    void clean() {
        moderationRequestRepository.deleteAll();
    }
}
