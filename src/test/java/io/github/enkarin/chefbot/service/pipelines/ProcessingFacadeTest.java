package io.github.enkarin.chefbot.service.pipelines;

import io.github.enkarin.chefbot.dto.BotAnswer;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.StandardUserAnswerOption;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.pipelinehandlers.ProcessingFacade;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import io.github.enkarin.chefbot.service.ExcludeUserProductsService;
import io.github.enkarin.chefbot.service.SearchFilterService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.stream.Stream;

import static io.github.enkarin.chefbot.enums.StandardUserAnswerOption.DISH_TYPES_WITH_ANY_CASE;
import static io.github.enkarin.chefbot.enums.StandardUserAnswerOption.SEARCH_DISH_OPTIONS;
import static io.github.enkarin.chefbot.enums.StandardUserAnswerOption.YES_NO_OR_ANY;
import static io.github.enkarin.chefbot.enums.StandardUserAnswerOption.YES_OR_NO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessingFacadeTest extends TestBase {
    @Autowired
    private ProcessingFacade processingFacade;

    @Autowired
    private SearchFilterRepository searchFilterRepository;

    @Autowired
    private SearchFilterService searchFilterService;

    @Autowired
    private ModerationRequestRepository moderationRequestRepository;

    @Autowired
    private ExcludeUserProductsService excludeUserProductsService;

    @Test
    void execute() {
        userRepository.save(User.builder().id(USER_ID).chatId(CHAT_ID).username(USERNAME).chatStatus(ChatStatus.APPROVE_BACK_TO_MAIN_MENU).build());

        assertThat(processingFacade.execute(USER_ID, "Да").botAnswer().messageText()).isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @Test
    void executeWithNewDishNeedPublish() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.DISH_NEED_PUBLISH);

        assertThat(processingFacade.execute(USER_ID, "aboba").botAnswer().messageText())
                .isEqualTo("""
                        Хотите опубликовать это блюдо?
                        Когда оно пройдёт модерацию, то станет доступно всем пользователям.
                        Блюдо останется доступно вам вне зависимости от результата модерации.
                        """);
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);
    }

    @Test
    void goToStatus() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);

        assertThat(processingFacade.goToStatus(USER_ID, ChatStatus.APPROVE_BACK_TO_MAIN_MENU)).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Вы хотите вернуться в главное меню?");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).isEqualTo(YES_OR_NO.getAnswers());
        });

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.APPROVE_BACK_TO_MAIN_MENU);
    }

    @Test
    void undo() {
        userRepository.save(User.builder()
                .id(USER_ID)
                .chatId(CHAT_ID)
                .username(USERNAME)
                .chatStatus(ChatStatus.APPROVE_BACK_TO_MAIN_MENU)
                .previousChatStatus(ChatStatus.MAIN_MENU)
                .build());

        assertThat(processingFacade.undo(USER_ID).messageText()).isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
    }

    @ParameterizedTest
    @MethodSource("provideStatusAndAnswer")
    void goToStatusShouldWork(final ChatStatus status, final String messageText, final StandardUserAnswerOption userAnswerOption) {
        createUser(status);
        searchFilterService.createSearchFilter(USER_ID);
        searchFilterService.putSpicySign(USER_ID, false);
        searchFilterService.putDishType(USER_ID, DishType.SOUP);
        initDishes();

        assertThat(processingFacade.goToStatus(USER_ID, status))
                .extracting(BotAnswer::messageText, BotAnswer::userAnswerOptions)
                .containsOnly(messageText, Optional.of(userAnswerOption.getAnswers()));
    }

    static Stream<Arguments> provideStatusAndAnswer() {
        return Stream.of(
                Arguments.of(ChatStatus.SELECT_DISH_PUBLISHED, "Выберите режим поиска", SEARCH_DISH_OPTIONS),
                Arguments.of(ChatStatus.SELECT_DISH_TYPE, "Выберете тип искомого блюда", DISH_TYPES_WITH_ANY_CASE),
                Arguments.of(ChatStatus.SELECT_DISH_SPICY, "Острое блюдо?", YES_NO_OR_ANY),
                Arguments.of(ChatStatus.SELECT_DISH_KITCHEN, "Выберите кухню мира:", StandardUserAnswerOption.CUISINES_WITH_ANY_CASE),
                Arguments.of(ChatStatus.EXECUTE_RANDOM_SEARCH, "*fifth:*\n-fifthProduct", StandardUserAnswerOption.MORE_OR_STOP),
                Arguments.of(ChatStatus.EXECUTE_SEARCH, "*fifth:*\n-fifthProduct", StandardUserAnswerOption.MORE_OR_STOP)
        );
    }

    @Test
    void goToStatusShouldWorkWithExecuteSearch() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.SELECT_DISH_TYPE);
        initDishes();

        processingFacade.execute(USER_ID, "Закуска"); //select soup
        processingFacade.execute(USER_ID, "нет"); //select spicy
        processingFacade.execute(USER_ID, "Славянская"); //select cuisine
        final BotAnswer userMessage = processingFacade.execute(USER_ID, "все блюда").botAnswer(); //select published

        assertThat(userMessage)
                .isNotNull()
                .extracting(BotAnswer::messageText)
                .isEqualTo("""
                        *third:*
                        -thirdProduct""");
    }

    @Test
    void searchRandomDishPipeline() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.SELECT_DISH_TYPE);
        initDishes();

        processingFacade.execute(USER_ID, "Любое"); //select type
        processingFacade.execute(USER_ID, "нет"); //select spicy
        processingFacade.execute(USER_ID, "Славянская"); //select cuisine
        final BotAnswer userMessage = processingFacade.execute(USER_ID, "случайное блюдо").botAnswer(); //select published

        assertThat(userMessage)
                .isNotNull()
                .extracting(BotAnswer::messageText)
                .isEqualTo("""
                        *third:*
                        -thirdProduct""");
        assertThat(userService.findUser(USER_ID).getSearchPageNumber()).isZero();
    }

    @Test
    void goToStatusShouldThrowExceptionWhenDishNotFound() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.SELECT_DISH_TYPE);

        processingFacade.execute(USER_ID, "да"); //select soup
        processingFacade.execute(USER_ID, "нет"); //select spicy
        processingFacade.execute(USER_ID, "Славянская"); //select cuisine
        assertThatThrownBy(() -> processingFacade.execute(USER_ID, "все блюда")) //select published
                .isInstanceOf(DishesNotFoundException.class)
                .hasMessage("Подходящие блюда не найдены. Вы возвращены в главное меню.");
    }

    @Test
    void goToStatusShouldWorkWithAddDish() {
        createUser(ChatStatus.NEW_DISH_NAME);

        final String dishName = "Кимчи суп";
        processingFacade.execute(USER_ID, dishName); //enter dish name
        processingFacade.execute(USER_ID, "Суп"); //enter is soup
        processingFacade.execute(USER_ID, "Да"); //enter is spicy
        processingFacade.execute(USER_ID, "Азиатская"); //enter cuisine
        processingFacade.execute(USER_ID, "кимчи, свинина, репчатый лук, перцовая паста кочудян, тофу"); //enter foodstuff
        processingFacade.execute(USER_ID, "нет"); //enter is need recipe
        processingFacade.execute(USER_ID, "нет"); //enter is need publish

        assertThat(dishRepository.findAll())
                .hasSize(1)
                .first()
                .satisfies(d -> {
                    assertThat(d.getDishName()).isEqualTo(dishName);
                    assertThat(d.getType()).isEqualTo(DishType.SOUP);
                    assertThat(d.isSpicy()).isTrue();
                    assertThat(d.getCuisine()).isEqualTo(WorldCuisine.ASIA);
                });
        assertThat(productRepository.findAll())
                .hasSize(5)
                .extracting(Product::getProductName)
                .containsOnly("Кимчи", "Свинина", "Репчатый лук", "Перцовая паста кочудян", "Тофу");
    }

    @Test
    void enrichingRecipesPipelineTest() {
        createUser(ChatStatus.ENRICHING_RECIPES);
        initDishes();

        processingFacade.execute(USER_ID, "sixth");
        processingFacade.execute(USER_ID, "Тушить долго");

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.getRecipe()).isEqualTo("Тушить долго");
            assertThat(dish.isPublished()).isFalse();
            assertThat(dish.getDishName()).isEqualTo("sixth");
        });
        assertThat(moderationRequestRepository.findAll()).isEmpty();
        assertThat(userService.findUser(USER_ID).getSearchPageNumber()).isZero();
    }

    @Test
    void editDishSpicyPipelineTest() {
        createUser(ChatStatus.SELECT_EDITING_DISH_NAME);
        initDishes();

        assertThat(processingFacade.execute(USER_ID, "fifth").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Укажите поле, которое вы хотите отредактировать");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("Название", "Острота", "Тип", "Кухня", "Список продуктов", "Рецепт");
        });
        assertThat(processingFacade.execute(USER_ID, "Острота").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Блюдо острое?");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("Да", "Нет");
        });
        processingFacade.execute(USER_ID, "Да");
        processingFacade.execute(USER_ID, "Нет");

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.isSpicy()).isTrue();
            assertThat(dish.isPublished()).isFalse();
            assertThat(dish.getDishName()).isEqualTo("fifth");
        });
        assertThat(moderationRequestRepository.count()).isEqualTo(0);
    }

    @Test
    void editDishNamePipelineTest() {
        createUser(ChatStatus.SELECT_EDITING_DISH_NAME);
        initDishes();

        assertThat(processingFacade.execute(USER_ID, "sixth").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Укажите поле, которое вы хотите отредактировать");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("Название", "Острота", "Тип", "Кухня", "Список продуктов", "Рецепт");
        });
        assertThat(processingFacade.execute(USER_ID, "название").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Введите новое название блюда");
            assertThat(botAnswer.userAnswerOptions()).isEmpty();
        });
        processingFacade.execute(USER_ID, "super dish!");
        processingFacade.execute(USER_ID, "Нет");

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).extracting(Dish::getDishName).contains("super dish!");
        assertThat(moderationRequestRepository.count()).isEqualTo(0);
    }

    @Test
    void editDishTypePipelineTest() {
        createUser(ChatStatus.SELECT_EDITING_DISH_NAME);
        initDishes();

        assertThat(processingFacade.execute(USER_ID, "fifth").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Укажите поле, которое вы хотите отредактировать");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("Название", "Острота", "Тип", "Кухня", "Список продуктов", "Рецепт");
        });
        assertThat(processingFacade.execute(USER_ID, "Тип").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Выберете новый тип блюда");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly(StandardUserAnswerOption.DISH_TYPES.getAnswers());
        });
        processingFacade.execute(USER_ID, "Суп");
        processingFacade.execute(USER_ID, "Нет");

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.getType()).isEqualTo(DishType.SOUP);
            assertThat(dish.isPublished()).isFalse();
            assertThat(dish.getDishName()).isEqualTo("fifth");
        });
        assertThat(moderationRequestRepository.count()).isEqualTo(0);
    }

    @Test
    void editDishKitchenPipelineTest() {
        createUser(ChatStatus.SELECT_EDITING_DISH_NAME);
        initDishes();

        assertThat(processingFacade.execute(USER_ID, "sixth").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Укажите поле, которое вы хотите отредактировать");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("Название", "Острота", "Тип", "Кухня", "Список продуктов", "Рецепт");
        });
        assertThat(processingFacade.execute(USER_ID, "Кухня").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Введите кухню, к которой относится блюдо");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly(StandardUserAnswerOption.CUISINES.getAnswers());
        });
        processingFacade.execute(USER_ID, "международная");
        processingFacade.execute(USER_ID, "Да");

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.getCuisine()).isEqualTo(WorldCuisine.INTERNATIONAL);
            assertThat(dish.isPublished()).isFalse();
            assertThat(dish.getDishName()).isEqualTo("sixth");
        });
        assertThat(moderationRequestRepository.count()).isEqualTo(1);
    }

    @Test
    void editDishFoodstuffPipelineTest() {
        createUser(ChatStatus.SELECT_EDITING_DISH_NAME);
        initDishes();

        assertThat(processingFacade.execute(USER_ID, "sixth").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Укажите поле, которое вы хотите отредактировать");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("Название", "Острота", "Тип", "Кухня", "Список продуктов", "Рецепт");
        });
        assertThat(processingFacade.execute(USER_ID, "Список продуктов").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("""
                Введите список продуктов для приготовления блюда одним сообщением.
                Отделяйте их запятой или новой строкой.
                Количество продукта нужно написать после его названия, отделив двоеточием или тире.""");
            assertThat(botAnswer.userAnswerOptions()).isEmpty();
        });
        processingFacade.execute(USER_ID, "Булочка, сосиска");
        processingFacade.execute(USER_ID, "Нет");

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.isPublished()).isFalse();
            assertThat(dish.getDishName()).isEqualTo("sixth");
        });
        assertThat(productRepository.findAll()).extracting(Product::getProductName).contains("Булочка", "Сосиска");
        assertThat(moderationRequestRepository.count()).isEqualTo(0);
    }

    @Test
    void editDishRecipePipelineTest() {
        createUser(ChatStatus.SELECT_EDITING_DISH_NAME);
        initDishes();

        assertThat(processingFacade.execute(USER_ID, "fifth").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Укажите поле, которое вы хотите отредактировать");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly("Название", "Острота", "Тип", "Кухня", "Список продуктов", "Рецепт");
        });
        assertThat(processingFacade.execute(USER_ID, "Рецепт").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Введите рецепт");
            assertThat(botAnswer.userAnswerOptions()).isEmpty();
        });
        processingFacade.execute(USER_ID, "Мощно прожарить");
        processingFacade.execute(USER_ID, "Да");

        assertThat(userService.findUser(USER_ID).getChatStatus()).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.getRecipe()).isEqualTo("Мощно прожарить");
            assertThat(dish.isPublished()).isFalse();
            assertThat(dish.getDishName()).isEqualTo("fifth");
        });
        assertThat(moderationRequestRepository.count()).isEqualTo(1);
    }

    @Test
    void addDishWithRecipePipelineTest() {
        createUser(ChatStatus.NEW_DISH_NAME);

        final String dishName = "Сырник";
        processingFacade.execute(USER_ID, dishName); //enter dish name
        processingFacade.execute(USER_ID, "Закуска"); //enter is soup
        processingFacade.execute(USER_ID, "Нет"); //enter is spicy
        processingFacade.execute(USER_ID, "Международная"); //enter cuisine
        processingFacade.execute(USER_ID, "Творог, сахар"); //enter foodstuff
        processingFacade.execute(USER_ID, "да"); //enter is need recipe
        processingFacade.execute(USER_ID, "Жарить на сковородке"); //enter is recipe
        processingFacade.execute(USER_ID, "нет"); //enter is need publish

        assertThat(dishRepository.findAll())
                .hasSize(1)
                .first()
                .satisfies(d -> {
                    assertThat(d.getDishName()).isEqualTo(dishName);
                    assertThat(d.getType()).isEqualTo(DishType.SNACK);
                    assertThat(d.isSpicy()).isFalse();
                    assertThat(d.getCuisine()).isEqualTo(WorldCuisine.INTERNATIONAL);
                    assertThat(d.getRecipe()).isEqualTo("Жарить на сковородке");
                });
        assertThat(productRepository.findAll()).extracting(Product::getProductName).containsOnly("Творог", "Сахар");
    }

    @Test
    void searchByProductPipelineTest() {
        createUser(ChatStatus.MAIN_MENU);
        initDishes();

        assertThat(processingFacade.goToStatus(USER_ID, ChatStatus.REQUEST_PRODUCTS_FOR_FIND_DISH)).satisfies(botAnswer -> {
            assertThat(botAnswer.userAnswerOptions()).isEmpty();
            assertThat(botAnswer.messageText()).isEqualTo("Введите список продуктов, которые должно содержать желаемое блюдо");
        });
        assertThat(processingFacade.execute(USER_ID, "secondProduct").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("*second:*\n-secondProduct");
            assertThat(botAnswer.userAnswerOptions().orElseThrow()).containsOnly(StandardUserAnswerOption.MORE_OR_STOP.getAnswers());
        });
        assertThat(processingFacade.execute(USER_ID, "Вернуться в главное меню").botAnswer()).satisfies(botAnswer -> {
            assertThat(botAnswer.messageText()).isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
            assertThat(botAnswer.userAnswerOptions()).isEmpty();
        });
        assertThat(userService.findUser(USER_ID).getSearchPageNumber()).isZero();
    }

    @Test
    void pageNumberShouldDropAfterUndoToProcessingSearchPublishedService() {
        userService.createOrUpdateUser(USER_ID, CHAT_ID, USERNAME);
        userService.switchToNewStatus(USER_ID, ChatStatus.SELECT_DISH_TYPE);
        initDishes();

        processingFacade.execute(USER_ID, "Суп"); //select type
        processingFacade.execute(USER_ID, "нет"); //select spicy
        processingFacade.execute(USER_ID, "Ближневосточная"); //select cuisine
        assertThat(processingFacade.execute(USER_ID, "Все блюда").botAnswer().messageText()).isEqualTo("""
                        *fifth:*
                        -fifthProduct""");
        processingFacade.undo(USER_ID);
        assertThat(processingFacade.execute(USER_ID, "Все личные блюда").botAnswer().messageText()).isEqualTo("""
                        *fifth:*
                        -fifthProduct""");
    }

    @Test
    void addExcludeProduct() {
        createUser(ChatStatus.EXCLUDE_PRODUCTS);
        initDishes();

        assertThat(processingFacade.execute(USER_ID, "добавить продукты в список").botAnswer().messageText())
                .isEqualTo("Введите названия продуктов, которые хотите добавить в список, через запятую или с новой строки");
        assertThat(processingFacade.execute(USER_ID, "first, second").botAnswer().messageText())
                .isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("firstProduct", "secondProduct");
    }

    @Test
    void deleteExcludeProductByName() {
        createUser(ChatStatus.EXCLUDE_PRODUCTS);
        initDishes();
        excludeUserProductsService.addExcludeProducts(USER_ID, "first", "second", "third");

        assertThat(processingFacade.execute(USER_ID, "удалить продукты из списка по полному названию").botAnswer().messageText())
                .isEqualTo("Введите названия продуктов, которые хотите исключить из списка, через запятую или с новой строки");
        assertThat(processingFacade.execute(USER_ID, "firstProduct, secondProduct").botAnswer().messageText())
                .isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("thirdProduct");
    }

    @Test
    void deleteExcludeProductContainsName() {
        createUser(ChatStatus.EXCLUDE_PRODUCTS);
        initDishes();
        excludeUserProductsService.addExcludeProducts(USER_ID, "first", "second", "third");

        assertThat(processingFacade.execute(USER_ID, "удалить продукты из списка по частичному названию").botAnswer().messageText())
                .isEqualTo("Введите названия продуктов, которые хотите исключить из списка, через запятую или с новой строки");
        assertThat(processingFacade.execute(USER_ID, "first, third").botAnswer().messageText())
                .isEqualTo("Вы в главном меню. Выберете следующую команду для выполнения.");
        assertThat(excludeUserProductsService.findExcludeProducts(USER_ID)).containsOnly("secondProduct");
    }
}
