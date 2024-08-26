package io.github.enkarin.chefbot.uicomponents;

import io.github.enkarin.chefbot.enums.UserAnswerOption;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FormatedReplyKeyboardMarkupTest extends TestBase {
    @Test
    void createKeyboardWithEvenElementQuantity() {
        final FormatedReplyKeyboardMarkup keyboardMarkup = new FormatedReplyKeyboardMarkup(UserAnswerOption.YES_OR_NO);

        assertThat(keyboardMarkup.getKeyboard().size()).isEqualTo(1);
        assertThat(keyboardMarkup.getKeyboard().get(0)).extracting(KeyboardButton::getText).containsAll(List.of("Да", "Нет"));
    }

    @Test
    void createKeyboardWithUnevenElementQuantity() {
        final FormatedReplyKeyboardMarkup keyboardMarkup = new FormatedReplyKeyboardMarkup(UserAnswerOption.CUISINES);

        assertThat(keyboardMarkup.getKeyboard().size()).isEqualTo(4);
        assertThat(keyboardMarkup.getKeyboard().get(0)).extracting(KeyboardButton::getText).containsAll(List.of("Азиатская", "Средиземноморская"));
        assertThat(keyboardMarkup.getKeyboard().get(1)).extracting(KeyboardButton::getText).containsAll(List.of("Международная", "Ближневосточная"));
        assertThat(keyboardMarkup.getKeyboard().get(2)).extracting(KeyboardButton::getText).containsAll(List.of("Мексиканская", "Славянская"));
        assertThat(keyboardMarkup.getKeyboard().get(3)).extracting(KeyboardButton::getText).containsAll(List.of("Что-то необычное", "Любая"));
    }
}
