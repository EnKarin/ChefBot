package io.github.enkarin.chefbot.mappers;

import io.github.enkarin.chefbot.dto.RequestMessageInfoDto;
import io.github.enkarin.chefbot.entity.RequestMessageInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMessageInfoEntityDtoMapper {
    RequestMessageInfo dtoToEntity(RequestMessageInfoDto requestMessageInfoDto);
}
