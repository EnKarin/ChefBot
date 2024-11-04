package io.github.enkarin.chefbot.pipelinehandlers.exclude;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class ShowExcludeProductsInputHandlerTest extends TestBase {
    @Autowired
    private ShowExcludeProductsInputHandler handler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void executeWithToMainMenu() {
        assertThat(handler.execute(USER_ID, "В главное меню").chatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithAddProduct() {
        assertThat(handler.execute(USER_ID, "добавить продукты в список").chatStatus()).isEqualTo(ChatStatus.ADD_EXCLUDE_PRODUCTS);
    }

    @Test
    void executeWithDeleteContainsName() {
        assertThat(handler.execute(USER_ID, "Удалить продукты из списка по частичному названию").chatStatus()).isEqualTo(ChatStatus.DELETE_EXCLUDE_PRODUCTS_CONTAINS_NAME);
    }

    @Test
    void executeWithDeleteByName() {
        assertThat(handler.execute(USER_ID, "Удалить продукты из списка по полному названию").chatStatus()).isEqualTo(ChatStatus.DELETE_EXCLUDE_PRODUCTS_BY_NAME);
    }

    @Test
    void getMessageForUserWhereExistsExcludeProducts() {
        createUser(ChatStatus.EXCLUDE_PRODUCTS);
        initDishes();
        jdbcTemplate.update("insert into user_exclude_product(user_id, product_name) values (?, 'thirdProduct'), (?, 'secondProduct')", USER_ID, USER_ID);

        assertThat(handler.getMessageForUser(USER_ID)).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).startsWith("Список продуктов, блюда с которыми будут исключены из поиска:\n");
            assertThat(botAnswer.messageText()).contains("-thirdProduct", "-secondProduct");
            assertThat(botAnswer.messageText()).endsWith("Желаете его изменить?");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("В главное меню",
                    "Добавить продукты в список",
                    "Удалить продукты из списка по полному названию",
                    "Удалить продукты из списка по частичному названию");
        });
    }

    @Test
    void getMessageForUserWithoutExistsExcludeProducts() {
        createUser(ChatStatus.EXCLUDE_PRODUCTS);

        assertThat(handler.getMessageForUser(USER_ID)).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("У вас нет продуктов, блюда с которыми будут исключены из поиска. Хотите добавить?");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("В главное меню", "Добавить продукты в список");
        });
    }
}
