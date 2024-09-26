package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.controllers.MainMenuCommandHandler;
import io.github.enkarin.chefbot.controllers.pipelines.ProcessingFacade;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchRecipeCommandHandler implements MainMenuCommandHandler {
    private final ProcessingFacade processingFacade;

    @Override
    public BotAnswer execute(final long userId) {
        return processingFacade.goToStatus(userId, ChatStatus.SELECT_DISH_TYPE_WITH_RECIPE_SEARCH);
    }

    @Override
    public String getCommandName() {
        return "/search_recipe";
    }
}
