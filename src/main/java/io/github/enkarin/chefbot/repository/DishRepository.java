package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DishRepository extends JpaRepository<Dish, Long> {
    @Query(nativeQuery = true, value = "select * from t_dish " +
            "where (published OR user_id=?1) AND (?2 is null OR spicy=?2) AND (?3 is null OR type=?3) AND (?4 is null OR cuisine=?4) " +
            "limit 5 offset ?5*5")
    Set<Dish> findAllDishByFilterWithSpecifiedOffset(long ownerId, Boolean spicy, String type, String cuisine, int page);

    @Query(nativeQuery = true, value = "select * from t_dish " +
            "where (published OR user_id=?1) AND (?2 is null OR spicy=?2) AND (?3 is null OR type=?3) AND (?4 is null OR cuisine=?4) " +
            "limit 1 offset ?5")
    Dish findDishByFilterWithSpecifiedOffset(long ownerId, Boolean spicy, String type, String cuisine, int offset);

    @Query(nativeQuery = true, value = "select count(*) from t_dish " +
            "where (published OR user_id=?1) AND (?2 is null OR spicy=?2) AND (?3 is null OR type=?3) AND (?4 is null OR cuisine=?4)")
    int countDishWithFilter(long ownerId, Boolean spicy, String type, String cuisine);

    @Query(nativeQuery = true, value = "select * from t_dish " +
            "where (published OR user_id=?1) AND (?2 is null OR spicy=?2) AND (?3 is null OR type=?3) AND (?4 is null OR cuisine=?4) AND recipe is not null " +
            "limit 5 offset ?5*5")
    Set<Dish> findAllDishByFilterWithSpecifiedOffsetAndRecipe(long ownerId, Boolean spicy, String type, String cuisine, int page);

    @Query(nativeQuery = true, value = "select * from t_dish " +
            "where (published OR user_id=?1) AND (?2 is null OR spicy=?2) AND (?3 is null OR type=?3) AND (?4 is null OR cuisine=?4) AND recipe is not null " +
            "limit 1 offset ?5")
    Dish findDishByFilterWithSpecifiedOffsetAndRecipe(long ownerId, Boolean spicy, String type, String cuisine, int offset);

    @Query(nativeQuery = true, value = "select count(*) from t_dish " +
            "where (published OR user_id=?1) AND (?2 is null OR spicy=?2) AND (?3 is null OR type=?3) AND (?4 is null OR cuisine=?4) AND recipe is not null")
    int countDishWithFilterAndRecipe(long ownerId, Boolean spicy, String type, String cuisine);

    @Query(value = "select * from t_dish where dish_name ILIKE CONCAT('%',?,'%') AND (published OR user_id=?)", nativeQuery = true)
    List<Dish> findByDishName(String nameSubstring, long userId);

    Optional<Dish> findByDishNameIgnoreCaseAndOwner(String dishName, User owner);
}
