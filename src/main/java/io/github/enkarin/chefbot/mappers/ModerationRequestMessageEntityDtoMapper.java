package io.github.enkarin.chefbot.mappers;

import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModerationRequestMessageEntityDtoMapper {
    ModerationRequestMessage dtoToEntity(ModerationRequestMessageDto moderationRequestMessageDto);

    ModerationRequestMessageDto entityToDto(ModerationRequestMessage moderationRequestMessage);
}
