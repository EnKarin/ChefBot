package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.pipelines.ProcessingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditDishCommand implements MainMenuCommand {
    private final ProcessingFacade processingFacade;

    @Override
    public BotAnswer execute(final long userId) {
        return processingFacade.goToStatus(userId, ChatStatus.SELECT_EDITING_DISH_NAME);
    }

    @Override
    public String getCommandName() {
        return "/edit_dish";
    }
}
