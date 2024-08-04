package io.github.enkarin.chefbot.mappers;

import io.github.enkarin.chefbot.dto.DishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DishEntityDtoMapper {

    @Mapping(target = "name", source = "dishName")
    @Mapping(target = "worldCuisine", source = "cuisine")
    DishDto entityToDto(Dish dish);

    Set<String> productsToString(Set<Product> products);

    default String productToString(Product product) {
        return product.getProductName();
    }
}
