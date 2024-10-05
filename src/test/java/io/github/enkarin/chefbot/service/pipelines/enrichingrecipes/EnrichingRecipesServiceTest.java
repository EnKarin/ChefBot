package io.github.enkarin.chefbot.service.pipelines.enrichingrecipes;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.pipelinehandlers.enrichingrecipes.EnrichingRecipesService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnrichingRecipesServiceTest extends TestBase {
    @Autowired
    private EnrichingRecipesService enrichingRecipesService;

    @Test
    void execute() {
        createUser(ChatStatus.ENRICHING_RECIPES);
        initDishes();

        assertThat(enrichingRecipesService.execute(USER_ID, "sixth").chatStatus()).isEqualTo(ChatStatus.EXISTS_DISH_PUT_RECIPE);
        assertThat(userService.findUser(USER_ID).getEditabledDish().getDishName()).isEqualTo("sixth");
    }

    @Test
    void executeWithOtherAnswer() {
        createUser(ChatStatus.ENRICHING_RECIPES);

        assertThat(enrichingRecipesService.execute(USER_ID, "Другое").chatStatus()).isEqualTo(ChatStatus.ENRICHING_RECIPES);
    }

    @Test
    void getMessageForUserWithSmallResult() {
        createUser(ChatStatus.ENRICHING_RECIPES);
        initDishes();

        assertThat(enrichingRecipesService.getMessageForUser(USER_ID).userAnswerOptions().orElseThrow()).containsOnly("fifth", "sixth");
    }

    @Test
    void getMessageForUserWithFullResult() {
        createUser(ChatStatus.ENRICHING_RECIPES);
        initDishes();
        initMoreDish();

        assertThat(enrichingRecipesService.getMessageForUser(USER_ID).userAnswerOptions().orElseThrow())
                .containsOnly("fifth", "sixth", "eighth", "ninth", "tenth", "eleventh", "twelfth", "thirteenth", "fourteenth", "fifteenth", "Другое");
    }

    @Test
    void getMessageForUserWithFullResultReturnWithoutRepeat() {
        createUser(ChatStatus.ENRICHING_RECIPES);
        initDishes();
        initMoreDish();
        dishRepository.save(Dish.builder()
                .dishName("sixteenth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());

        assertThat(enrichingRecipesService.getMessageForUser(USER_ID).userAnswerOptions().orElseThrow())
                .containsOnly("fifth", "sixth", "eighth", "ninth", "tenth", "eleventh", "sixteenth", "thirteenth", "fourteenth", "fifteenth", "Другое");
        assertThat(enrichingRecipesService.getMessageForUser(USER_ID).userAnswerOptions().orElseThrow()).containsOnly("twelfth");
    }

    private void initMoreDish() {
        dishRepository.save(Dish.builder()
                .dishName("eighth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("ninth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("tenth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("eleventh")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("twelfth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("thirteenth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("fourteenth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
        dishRepository.save(Dish.builder()
                .dishName("fifteenth")
                .type(DishType.SOUP)
                .spicy(false)
                .cuisine(WorldCuisine.MIDDLE_EASTERN)
                .owner(userRepository.findById(USER_ID).orElseThrow())
                .build());
    }

    @Test
    void getMessageForUserWithEmptyResult() {
        createUser(ChatStatus.ENRICHING_RECIPES);

        assertThatThrownBy(() -> enrichingRecipesService.getMessageForUser(USER_ID)).isInstanceOf(DishesNotFoundException.class);
    }
}
