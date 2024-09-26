package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.controllers.MainMenuCommandHandler;
import io.github.enkarin.chefbot.dto.BotAnswer;
import org.springframework.stereotype.Component;

@Component
public class UndoCommandHandler implements MainMenuCommandHandler {

    @Override
    public BotAnswer execute(final long userId) {
        return new BotAnswer("Эта команда не доступна в главном меню");
    }

    @Override
    public String getCommandName() {
        return "/undo";
    }
}
