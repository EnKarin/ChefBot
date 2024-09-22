package io.github.enkarin.chefbot.controllers.mainmenucommands;

import io.github.enkarin.chefbot.dto.BotAnswer;

public interface MainMenuCommand {
    BotAnswer execute(long userId);
    String getCommandName();
}
