package io.github.enkarin.chefbot.mappers;

import io.github.enkarin.chefbot.dto.ModerationDishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ProductQuantity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DishEntityModerationDtoMapper {
    @Mapping(target = "name", source = "dishName")
    @Mapping(target = "worldCuisine", source = "cuisine")
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "ownerChatId", source = "owner.chatId")
    ModerationDishDto entityToDto(Dish dish);

    Set<String> productsToString(Set<ProductQuantity> products);

    default String productToString(final ProductQuantity productQuantity) {
        return productQuantity.getProduct().getProductName();
    }
}
