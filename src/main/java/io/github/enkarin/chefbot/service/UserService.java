package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void createOfUpdateUser(final long userId, final long chatId, final String username) {
        final User user = userRepository.findById(userId).orElseGet(() -> User.builder().id(userId).chatStatus(ChatStatus.MAIN_MENU).build());
        user.setChatId(chatId);
        user.setUsername(username);
        userRepository.save(user);
    }

    public Set<Long> getAllModerators() {
        return userRepository.findAllByModeratorIsTrue().stream()
                .map(User::getChatId)
                .collect(Collectors.toSet());
    }

    public ChatStatus getChatStatus(final long userId) {
        return findUser(userId).getChatStatus();
    }

    @Transactional
    public void setChatStatus(final long userId, final ChatStatus chatStatus) {
        findUser(userId).setChatStatus(chatStatus);
    }

    @Transactional
    public void backToMainMenu(final long userId) {
        final User user = findUser(userId);
        user.setEditabledDish(null);
        user.setChatStatus(ChatStatus.MAIN_MENU);
    }

    User findUser(final long userId) {
        return userRepository.findById(userId).orElseThrow();
    }
}
