package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchFilterService {
    private final SearchFilterRepository searchFilterRepository;
    private final UserService userService;
    private final DishRepository dishRepository;

    @Transactional
    void createSearchFilter(final long ownerId) {
        userService.findUser(ownerId).setSearchFilter(searchFilterRepository.save(new SearchFilter()));
    }

    @Transactional
    void deleteSearchFilter(final long ownerId) {
        final User user = userService.findUser(ownerId);
        final SearchFilter searchFilter = user.getSearchFilter();
        user.setSearchFilter(null);
        searchFilterRepository.delete(searchFilter);
    }

    @Transactional
    void putSoupSign(final long ownerId, final boolean soup) {
        userService.findUser(ownerId).getSearchFilter().setSoup(soup);
    }

    @Transactional
    void putSpicySign(final long ownerId, final boolean spicy) {
        userService.findUser(ownerId).getSearchFilter().setSpicy(spicy);
    }

    @Transactional
    void putKitchen(final long ownerId, final WorldCuisine cuisine) {
        userService.findUser(ownerId).getSearchFilter().setCuisine(cuisine);
    }

    @Transactional
    void putNeedPublicSearch(final long ownerId, final boolean searchFromPublicDish) {
        userService.findUser(ownerId).getSearchFilter().setSearchFromPublicDish(searchFromPublicDish);
    }

    Set<DisplayDishDto> searchDishWithCurrentFilter(final long ownerId) {
        final User currentUser = userService.findUser(ownerId);
        final SearchFilter searchFilter = currentUser.getSearchFilter();
        if (searchFilter.isSearchFromPublicDish()) {
            return dishRepository.findAllDishByFilter(currentUser.getId(),
                            searchFilter.getSpicy(),
                            searchFilter.getSoup(),
                            isNull(searchFilter.getCuisine()) ? null : searchFilter.getCuisine().name())
                    .stream()
                    .map(dish -> new DisplayDishDto(dish.getDishName(), dish.getProducts().stream().map(Product::getProductName).collect(Collectors.toSet())))
                    .collect(Collectors.toSet());
        } else {
            return currentUser.getDishes().stream()
                    .filter(dish -> (isNull(searchFilter.getSoup()) || searchFilter.getSoup() == dish.isSoup())
                            && (isNull(searchFilter.getSpicy()) || searchFilter.getSpicy() == dish.isSpicy())
                            && (isNull(searchFilter.getCuisine()) || searchFilter.getCuisine() == dish.getCuisine()))
                    .map(dish -> new DisplayDishDto(dish.getDishName(), dish.getProducts().stream().map(Product::getProductName).collect(Collectors.toSet())))
                    .collect(Collectors.toSet());
        }
    }
}
