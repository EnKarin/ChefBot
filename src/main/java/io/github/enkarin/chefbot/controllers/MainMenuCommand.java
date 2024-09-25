package io.github.enkarin.chefbot.controllers;

import io.github.enkarin.chefbot.dto.BotAnswer;

public interface MainMenuCommand {
    BotAnswer execute(long userId);
    String getCommandName();
}
