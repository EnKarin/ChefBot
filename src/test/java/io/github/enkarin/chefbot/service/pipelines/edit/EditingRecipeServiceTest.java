package io.github.enkarin.chefbot.service.pipelines.edit;

import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.service.DishService;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class EditingRecipeServiceTest extends TestBase {
    @Autowired
    private EditingRecipeService editingRecipeService;

    @Autowired
    private DishService dishService;

    @BeforeEach
    void init() {
        createUser(ChatStatus.NEW_DISH_RECIPE);
        dishService.initDishName(USER_ID, "Суп");
    }

    @Test
    void execute() {
        assertThat(editingRecipeService.execute(USER_ID, "Варить").chatStatus()).isEqualTo(ChatStatus.DISH_NEED_PUBLISH);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getRecipe()).isEqualTo("Варить");
    }

    @Test
    void executeWithTooLargeRecipe() {
        assertThat(editingRecipeService.execute(USER_ID, """
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
                """).chatStatus()).isEqualTo(ChatStatus.EDITING_RECIPE);
        assertThat(userRepository.findById(USER_ID).orElseThrow().getEditabledDish().getRecipe()).isNull();
    }
}
