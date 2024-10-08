package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.controllers.MainMenuCommandHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.pipelinehandlers.ProcessingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchByNameCommandHandler implements MainMenuCommandHandler {
    private final ProcessingFacade processingFacade;

    @Override
    public BotAnswer execute(final long userId) {
        return processingFacade.goToStatus(userId, ChatStatus.FIND_DISH_BY_NAME);
    }

    @Override
    public String getCommandName() {
        return "/search_by_name";
    }
}
