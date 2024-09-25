package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.controllers.MainMenuCommand;
import io.github.enkarin.chefbot.controllers.pipelines.ProcessingFacade;
import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchByProductsCommand implements MainMenuCommand {
    private final ProcessingFacade processingFacade;

    @Override
    public BotAnswer execute(final long userId) {
        return processingFacade.goToStatus(userId, ChatStatus.REQUEST_PRODUCTS_FOR_FIND_DISH);
    }

    @Override
    public String getCommandName() {
        return "/search_by_products";
    }
}
