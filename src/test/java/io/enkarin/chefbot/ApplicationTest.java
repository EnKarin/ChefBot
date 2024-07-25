package io.enkarin.chefbot;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest extends TestBase {

    @Autowired
    private ApplicationContext context;

    @Disabled("before add db")
    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }
}