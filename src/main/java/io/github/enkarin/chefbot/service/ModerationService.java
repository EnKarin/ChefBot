package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.dto.ModerationResultDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.mappers.DishEntityDtoMapper;
import io.github.enkarin.chefbot.mappers.ModerationRequestMessageEntityDtoMapper;
import io.github.enkarin.chefbot.repository.ModerationRequestMessageRepository;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ModerationService {
    private final UserService userService;
    private final ModerationRequestRepository moderationRequestRepository;
    private final ModerationRequestMessageRepository moderationRequestMessageRepository;
    private final ModerationRequestMessageEntityDtoMapper moderationRequestMessageEntityDtoMapper;
    private final DishEntityDtoMapper dishEntityDtoMapper;
    private final UserRepository userRepository;

    public ModerationDishDto createModerationRequest(final long userId) {
        final ModerationRequest moderationRequest = moderationRequestRepository.save(ModerationRequest.builder()
                .moderationDish(userService.findUser(userId).getEditabledDish())
                .build());
        final ModerationDishDto result = dishEntityDtoMapper.entityToDto(moderationRequest.getModerationDish());
        result.setRequestId(moderationRequest.getId());
        return result;
    }

    public void addRequestMessages(final long moderationRequestId, final Set<ModerationRequestMessageDto> moderationRequestMessageDtoSet) {
        final ModerationRequest currentRequest = moderationRequestRepository.findById(moderationRequestId).orElseThrow();
        moderationRequestMessageRepository.saveAll(moderationRequestMessageDtoSet.stream()
                .map(moderationRequestMessageEntityDtoMapper::dtoToEntity)
                .peek(moderationRequestMessage -> moderationRequestMessage.setCurrentModerationRequest(currentRequest))
                .collect(Collectors.toSet()));
    }

    Set<ModerationDishDto> findAllRequests() {
        return moderationRequestRepository.findAll().stream().map(moderationRequest -> {
            final ModerationDishDto dto = dishEntityDtoMapper.entityToDto(moderationRequest.getModerationDish());
            dto.setRequestId(moderationRequest.getId());
            return dto;
        }).collect(Collectors.toSet());
    }

    public ModerationResultDto approveRequest(final long requestId) {
        final ModerationRequest moderationRequest = moderationRequestRepository.findById(requestId).orElseThrow();
        final Dish moderationDish = moderationRequest.getModerationDish();
        moderationDish.setPublished(true);
        final ModerationResultDto resultDto = ModerationResultDto.createApproveResult(moderationRequest.getModerationDish().getDishName(),
                moderationRequest.getModerationDish().getOwner().getChatId(),
                moderationRequest.getModerationRequestMessages().stream().map(moderationRequestMessageEntityDtoMapper::entityToDto).collect(Collectors.toSet()));
        moderationRequestRepository.delete(moderationRequest);
        return resultDto;
    }

    public ModerationResultDto declineRequest(final long userId, final String cause) {
        final User user = userService.findUser(userId);
        final Dish moderableDish = user.getModerableDish();
        moderableDish.setPublished(false);
        final ModerationRequest moderationRequest = moderableDish.getModerationRequest();
        final ModerationResultDto moderationResultDto = ModerationResultDto.createDeclineResult(moderableDish.getDishName(),
                moderableDish.getOwner().getChatId(),
                moderationRequest.getModerationRequestMessages().stream().map(moderationRequestMessageEntityDtoMapper::entityToDto).collect(Collectors.toSet()),
                cause);
        user.setModerableDish(null);
        moderationRequestRepository.delete(moderationRequest);
        userRepository.save(user);
        return moderationResultDto;
    }

    public void startModerate(final long userId, final long requestId) {
        final User user = userService.findUser(userId);
        user.setModerableDish(moderationRequestRepository.findById(requestId).orElseThrow().getModerationDish());
    }
}
