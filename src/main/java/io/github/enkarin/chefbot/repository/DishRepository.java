package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {
}
