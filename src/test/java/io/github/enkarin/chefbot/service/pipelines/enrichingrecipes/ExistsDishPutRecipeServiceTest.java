package io.github.enkarin.chefbot.service.pipelines.enrichingrecipes;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.ModerationRequest;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.ModerationRequestRepository;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ExistsDishPutRecipeServiceTest extends TestBase {
    @Autowired
    private ExistsDishPutRecipeService existsDishPutRecipeService;

    @Autowired
    private ModerationRequestRepository moderationRequestRepository;

    @Autowired
    private DishService dishService;

    @BeforeEach
    void init() {
        createUser(ChatStatus.EXISTS_DISH_PUT_RECIPE);
        initDishes();
    }

    @Test
    void executeForPublish() {
        dishService.putEditableDish(USER_ID, "sixth");

        assertThat(existsDishPutRecipeService.execute(USER_ID, "Жарить")).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(dishRepository.findAll()).anySatisfy(dish -> {
            assertThat(dish.getRecipe()).isEqualTo("Жарить");
            assertThat(dish.isPublished()).isFalse();
            assertThat(dish.getDishName()).isEqualTo("sixth");
        });
        assertThat(moderationRequestRepository.findAll()).isEmpty();
    }

    @Test
    void executeForNonPublish() {
        final Dish dish = dishRepository.findAll().stream()
                .filter(d -> "fifth".equals(d.getDishName()))
                .findAny()
                .orElseThrow();
        dish.setPublished(true);
        dish.setOwner(userService.findUser(USER_ID));
        dishRepository.save(dish);
        dishService.putEditableDish(USER_ID, "fifth");

        assertThat(existsDishPutRecipeService.execute(USER_ID, "Варить")).isEqualTo(ChatStatus.MAIN_MENU);
        assertThat(moderationRequestRepository.findAll()).extracting(ModerationRequest::getModerationDish).anySatisfy(d -> {
            assertThat(d.getDishName()).isEqualTo("fifth");
            assertThat(d.isPublished()).isFalse();
            assertThat(d.getRecipe()).isEqualTo("Варить");
        });
    }

    @Test
    void executeWithTooLargeRecipe() {
        assertThat(existsDishPutRecipeService.execute(USER_ID, """
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                fafdsfjdsfjadsfjasdklfjasdfljdsfklasdjfkldsajfkasdjgaiowrjfn vqerjaiosnuifnrignifvnauiohqgiovnafvauhiwnfso[anvoihasinwjnuolsakndfsndfjasdnfkjaugnsjndsufahwunjsdn
                """)).isEqualTo(ChatStatus.EXISTS_DISH_PUT_RECIPE);
    }
}
