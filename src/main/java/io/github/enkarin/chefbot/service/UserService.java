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
    public User findOrSaveUser(final Long chatId) {
        return userRepository.findById(chatId).orElseGet(() -> userRepository.save(User.builder()
                .chatId(chatId)
                 .chatStatus(ChatStatus.MAIN_MENU)
                .build()));

    }

    @Transactional
    public void changeModeratorStatus(final Long chatId) {
        userRepository.findById(chatId).ifPresent(u -> u.setModerator(!u.isModerator()));
    }

    public Set<Long> getAllModerators() {
        return userRepository.findAllByModeratorIsTrue().stream()
                .map(User::getChatId)
                .collect(Collectors.toSet());
    }

    @Transactional
    public ChatStatus getChatStatus(final long chatId) {
        return findOrSaveUser(chatId).getChatStatus();
    }
}
