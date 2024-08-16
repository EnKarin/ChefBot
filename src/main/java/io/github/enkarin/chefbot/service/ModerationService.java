package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.RequestMessageInfoDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.mappers.DishEntityDtoMapper;
import io.github.enkarin.chefbot.mappers.RequestMessageInfoEntityDtoMapper;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.repository.RequestMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ModerationService {
    private final ModerationRequestRepository moderationRequestRepository;
    private final RequestMessageRepository requestMessageRepository;
    private final RequestMessageInfoEntityDtoMapper requestMessageInfoEntityDtoMapper;
    private final DishEntityDtoMapper dishEntityDtoMapper;

    @Transactional
    long createModerationRequest(final User author, final Dish moderableDish) {
        return moderationRequestRepository.save(ModerationRequest.builder()
                .fromUser(author)
                .moderationDish(moderableDish)
                .fresh(true)
                .build()).getId();
    }

    @Transactional
    void putRequestMessages(final long moderationRequestId, final Set<RequestMessageInfoDto> requestMessageInfoDtoSet) {
        moderationRequestRepository.findById(moderationRequestId).orElseThrow()
                .setModeratorsRequestMessageInfos(requestMessageRepository.saveAll(requestMessageInfoDtoSet.stream()
                        .map(requestMessageInfoEntityDtoMapper::dtoToEntity)
                        .collect(Collectors.toSet())));
    }

    @Transactional
    Set<ModerationDishDto> findAllFreshRequests() {
        final List<ModerationRequest> moderationRequests = moderationRequestRepository.findByFreshIsTrue();
        moderationRequests.forEach(moderationRequest -> moderationRequest.setFresh(false));
        return findAllRequests(moderationRequests);
    }

    Set<ModerationDishDto> findAllRequests() {
        return findAllRequests(moderationRequestRepository.findAll());
    }

    private Set<ModerationDishDto> findAllRequests(final List<ModerationRequest> moderationRequests) {
        return moderationRequests.stream().map(moderationRequest -> {
            final ModerationDishDto dto = dishEntityDtoMapper.entityToDto(moderationRequest.getModerationDish());
            dto.setRequestId(moderationRequest.getId());
            return dto;
        }).collect(Collectors.toSet());
    }

    //todo: realise approve and decline methods
}
