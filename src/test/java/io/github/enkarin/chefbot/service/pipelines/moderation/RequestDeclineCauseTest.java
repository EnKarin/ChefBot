package io.github.enkarin.chefbot.service.pipelines.moderation;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.ModerationService;
import io.github.enkarin.chefbot.util.ModerationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class RequestDeclineCauseTest extends ModerationTest {
    @Autowired
    private RequestDeclineCause requestDeclineCause;

    @Autowired
    private ModerationService moderationService;

    @Test
    void execute() {
        moderationInit();
        createUser(ChatStatus.NEW_DISH_NAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.WRITE_DECLINE_MODERATION_REQUEST);
        moderationService.startModerate(USER_ID, moderationRequestsId[0]);

        assertThat(requestDeclineCause.execute(USER_ID, "Bad product").chatStatus()).isEqualTo(ChatStatus.NEW_DISH_NAME);
        assertThat(userService.findUser(USER_ID).getModerableDish()).isNull();
    }
}
