package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    Set<User> findAllByModeratorIsTrueAndChatIdIsNot(long id);

    List<User> findAllByModerableDish(Dish dish);
}
