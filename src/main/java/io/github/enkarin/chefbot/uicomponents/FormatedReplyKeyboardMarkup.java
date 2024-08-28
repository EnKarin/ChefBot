package io.github.enkarin.chefbot.uicomponents;

import io.github.enkarin.chefbot.enums.UserAnswerOption;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serial;
import java.util.LinkedList;
import java.util.List;

public final class FormatedReplyKeyboardMarkup extends ReplyKeyboardMarkup {
    @Serial
    private static final long serialVersionUID = 3790890795798913977L;

    public FormatedReplyKeyboardMarkup(final UserAnswerOption answerOptions) {
        setOneTimeKeyboard(true);
        setResizeKeyboard(true);
        setIsPersistent(true);
        final List<KeyboardRow> keyboardRowList = new LinkedList<>();
        final String[] answers = answerOptions.getAnswers();
        final List<KeyboardButton> keyboardButtons = new LinkedList<>();
        for (int i = 0; i < answers.length;) {
            for (int j = 0; j < 2 && i < answers.length; j++, i++) {
                keyboardButtons.add(new KeyboardButton(answers[i]));
            }
            keyboardRowList.add(new KeyboardRow(keyboardButtons));
            keyboardButtons.clear();
        }
        setKeyboard(keyboardRowList);
    }
}
