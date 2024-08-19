package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void createOrUpdateUser(final long userId, final long chatId, final String username) {
        final User user = userRepository.findById(userId).orElseGet(() -> User.builder()
                .id(userId)
                .chatStatus(ChatStatus.MAIN_MENU)
                .dishes(new HashSet<>())
                .build());
        user.setChatId(chatId);
        user.setUsername(username);
        userRepository.save(user);
    }

    public ChatStatus getChatStatus(final long userId) {
        return findUser(userId).getChatStatus();
    }

    public Set<Long> getAllModeratorsWithoutCurrentUser(final long currentChatId) {
        return userRepository.findAllByModeratorIsTrueAndChatIdIsNot(currentChatId).stream()
                .map(User::getChatId)
                .collect(Collectors.toSet());
    }

    ChatStatus getPreviousChatStatus(final long userId) {
        return findUser(userId).getPreviousChatStatus();
    }

    @Transactional
    void switchToNewStatus(final long userId, final ChatStatus newChatStatus) {
        final User user = findUser(userId);
        if (user.getChatStatus() != newChatStatus) {
            if (newChatStatus == ChatStatus.MAIN_MENU) {
                user.setEditabledDish(null);
                user.setPreviousChatStatus(ChatStatus.MAIN_MENU);
            } else {
                user.setPreviousChatStatus(user.getChatStatus());
            }
            user.setChatStatus(newChatStatus);
        }
    }

    boolean canUndo(final long userId) {
        final User user = findUser(userId);
        return user.getChatStatus() != user.getPreviousChatStatus();
    }

    @Transactional
    ChatStatus backToPreviousStatus(final long userId) {
        final User user = findUser(userId);
        user.setChatStatus(user.getPreviousChatStatus());
        return user.getChatStatus();
    }

    User findUser(final long userId) {
        return userRepository.findById(userId).orElseThrow();
    }
}
