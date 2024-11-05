package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.controllers.MainMenuCommandHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.pipelinehandlers.ProcessingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrichingRecipesCommandHandler implements MainMenuCommandHandler {
    private final ProcessingFacade processingFacade;

    @Override
    public BotAnswer execute(final long userId) {
        try {
            return processingFacade.goToStatus(userId, ChatStatus.ENRICHING_RECIPES);
        } catch (DishesNotFoundException e) {
            processingFacade.goToStatus(userId, ChatStatus.MAIN_MENU);
            return new BotAnswer("У вас нет блюд без рецептов. Вы возвращены в главное меню");
        }
    }

    @Override
    public String getCommandName() {
        return "/enriching_recipes";
    }
}
