package io.github.enkarin.chefbot.mappers;

import io.github.enkarin.chefbot.dto.ModerationRequestMessageDto;
import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModerationRequestMessageEntityDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentModerationRequest", ignore = true)
    ModerationRequestMessage dtoToEntity(ModerationRequestMessageDto moderationRequestMessageDto);

    ModerationRequestMessageDto entityToDto(ModerationRequestMessage moderationRequestMessage);
}
