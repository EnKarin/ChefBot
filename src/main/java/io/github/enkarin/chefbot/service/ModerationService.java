package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationResultDto;
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
                .moderatorsRequestMessageInfos(List.of())
                .build()).getId();
    }

    @Transactional
    void addRequestMessages(final long moderationRequestId, final Set<RequestMessageInfoDto> requestMessageInfoDtoSet) {
        moderationRequestRepository.findById(moderationRequestId).orElseThrow()
                .getModeratorsRequestMessageInfos().addAll(requestMessageRepository.saveAll(requestMessageInfoDtoSet.stream()
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

    @Transactional
    ModerationResultDto approveRequest(final long requestId) {
        final ModerationRequest moderationRequest = moderationRequestRepository.findById(requestId).orElseThrow();
        final ModerationResultDto resultDto = ModerationResultDto.createApproveResult(moderationRequest.getModerationDish().getDishName(),
                moderationRequest.getFromUser().getChatId(),
                moderationRequest.getModeratorsRequestMessageInfos().stream().map(requestMessageInfoEntityDtoMapper::entityToDto).collect(Collectors.toSet()));
        moderationRequestRepository.delete(moderationRequest);
        return resultDto;
    }

    @Transactional
    ModerationResultDto declineRequest(final long requestId) {
        final ModerationRequest moderationRequest = moderationRequestRepository.findById(requestId).orElseThrow();
        final ModerationResultDto resultDto = ModerationResultDto.createDeclineResult(moderationRequest.getModerationDish().getDishName(),
                moderationRequest.getFromUser().getChatId(),
                moderationRequest.getModeratorsRequestMessageInfos().stream().map(requestMessageInfoEntityDtoMapper::entityToDto).collect(Collectors.toSet()));
        moderationRequestRepository.delete(moderationRequest);
        return resultDto;
    }

    private Set<ModerationDishDto> findAllRequests(final List<ModerationRequest> moderationRequests) {
        return moderationRequests.stream().map(moderationRequest -> {
            final ModerationDishDto dto = dishEntityDtoMapper.entityToDto(moderationRequest.getModerationDish());
            dto.setRequestId(moderationRequest.getId());
            return dto;
        }).collect(Collectors.toSet());
    }
}
