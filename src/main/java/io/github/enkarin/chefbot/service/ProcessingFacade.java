package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.enums.ChatStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcessingFacade {
    private final Map<ChatStatus, ProcessingService> processingServiceMap;

    public ProcessingFacade(final List<ProcessingService> processingServiceList) {
        processingServiceMap = processingServiceList.stream().collect(Collectors.toMap(ProcessingService::getCurrentStatus, Function.identity()));
    }

    public BotAnswer execute(final long chatId, final ChatStatus chatStatus, final String text) {
        return processingServiceMap.get(chatStatus).execute(chatId, text);
    }
}
