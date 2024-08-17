package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.dto.ModerationResultDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.mappers.DishEntityDtoMapper;
import io.github.enkarin.chefbot.mappers.ModerationRequestMessageEntityDtoMapper;
import io.github.enkarin.chefbot.repository.ModerationRequestMessageRepository;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
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
    private final ModerationRequestMessageRepository moderationRequestMessageRepository;
    private final ModerationRequestMessageEntityDtoMapper moderationRequestMessageEntityDtoMapper;
    private final DishEntityDtoMapper dishEntityDtoMapper;

    @Transactional
    void createModerationRequest(final Dish moderableDish) {
        moderationRequestRepository.save(ModerationRequest.builder()
                .moderationDish(moderableDish)
                .fresh(true)
                .build());
    }

    @Transactional
    void addRequestMessages(final long moderationRequestId, final Set<ModerationRequestMessageDto> moderationRequestMessageDtoSet) {
        final ModerationRequest currentRequest = moderationRequestRepository.findById(moderationRequestId).orElseThrow();
        currentRequest.getModerationRequestMessages().addAll(moderationRequestMessageRepository.saveAll(moderationRequestMessageDtoSet.stream()
                .map(moderationRequestMessageEntityDtoMapper::dtoToEntity)
                .peek(moderationRequestMessage -> moderationRequestMessage.setCurrentModerationRequest(currentRequest))
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
                moderationRequest.getModerationDish().getOwner().getChatId(),
                moderationRequest.getModerationRequestMessages().stream().map(moderationRequestMessageEntityDtoMapper::entityToDto).collect(Collectors.toSet()));
        moderationRequestRepository.delete(moderationRequest);
        return resultDto;
    }

    @Transactional
    ModerationResultDto declineRequest(final long requestId) {
        final ModerationRequest moderationRequest = moderationRequestRepository.findById(requestId).orElseThrow();
        final ModerationResultDto resultDto = ModerationResultDto.createDeclineResult(moderationRequest.getModerationDish().getDishName(),
                moderationRequest.getModerationDish().getOwner().getChatId(),
                moderationRequest.getModerationRequestMessages().stream().map(moderationRequestMessageEntityDtoMapper::entityToDto).collect(Collectors.toSet()));
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
