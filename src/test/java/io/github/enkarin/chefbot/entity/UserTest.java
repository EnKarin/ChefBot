package io.github.enkarin.chefbot.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testEquals() {
        assertThat(User.builder().id(11).build()).isEqualTo(User.builder().id(11).build());
    }

    @Test
    void testNotEquals() {
        assertThat(User.builder().id(12).chatId(1).username("usr").build()).isNotEqualTo(User.builder().id(11).chatId(1).username("usr").build());
    }

    @Test
    void testEqualsWithNull() {
        assertThat(User.builder().id(11).build()).isNotEqualTo(null);
    }
}
