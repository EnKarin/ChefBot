package io.github.enkarin.chefbot.entity;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void testEquals() {
        assertThat(Product.builder().productName("product1").build()).isEqualTo(Product.builder().productName("product1").build());
    }

    @Test
    void testNotEquals() {
        final User owner = new User();
        assertThat(Product.builder().productName("pr1").users(Set.of(owner)).build()).isNotEqualTo(Product.builder().productName("pr2").users(Set.of(owner)).build());
    }

    @Test
    void testEqualsWithNull() {
        assertThat(Product.builder().productName("prod").build()).isNotEqualTo(null);
    }

    @Test
    void testEqualsSelf() {
        final Product product = new Product();
        final Product product1 = product;
        assertThat(product).isEqualTo(product1);
    }
}
