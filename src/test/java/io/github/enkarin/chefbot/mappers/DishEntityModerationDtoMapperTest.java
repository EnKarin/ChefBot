package io.github.enkarin.chefbot.mappers;

import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.ProductQuantity;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class DishEntityModerationDtoMapperTest extends TestBase {
    @Autowired
    private DishEntityModerationDtoMapper mapper;

    @Test
    void productToString() {
        assertThat(mapper.productToString(ProductQuantity.builder().product(Product.builder().productName("bread").build()).build())).isEqualTo("bread");
    }
}
