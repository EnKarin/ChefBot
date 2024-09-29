package io.github.enkarin.chefbot.mappers;

import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DishEntityDtoMapperTest extends TestBase {
    @Autowired
    private DishEntityDtoMapper mapper;

    @Test
    void productToString() {
        assertThat(mapper.productToString(Product.builder().productName("bread").productQuantities(Set.of()).build())).isEqualTo("bread");
    }
}
