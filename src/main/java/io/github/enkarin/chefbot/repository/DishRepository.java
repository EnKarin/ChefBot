package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface DishRepository extends JpaRepository<Dish, Long> {
    @Query(nativeQuery = true, value = "select * from t_dish " +
            "where (published OR user_id=?1) AND (?2 is null OR spicy=?2) AND (?3 is null OR type=?3) AND (?4 is null OR cuisine=?4)" +
            "limit 5 offset ?5*5")
    Set<Dish> findAllDishByFilterWithSpecifiedOffset(long ownerId, Boolean spicy, String type, String cuisine, int page);
}
