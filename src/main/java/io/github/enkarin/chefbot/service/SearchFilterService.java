package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import io.github.enkarin.chefbot.exceptions.DishesNotFoundException;
import io.github.enkarin.chefbot.mappers.DishEntityToDisplayDtoMapper;
import io.github.enkarin.chefbot.repository.DishRepository;
import io.github.enkarin.chefbot.repository.SearchFilterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional(readOnly = true)
public class SearchFilterService {
    private final SearchFilterRepository searchFilterRepository;
    private final UserService userService;
    private final DishRepository dishRepository;
    private final DishEntityToDisplayDtoMapper dishEntityToDisplayDtoMapper;
    private final Random random = new Random();

    public SearchFilterService(final SearchFilterRepository searchFilterRepository,
                               final UserService userService,
                               final DishRepository dishRepository,
                               final DishEntityToDisplayDtoMapper dishEntityToDisplayDtoMapper) {
        this.searchFilterRepository = searchFilterRepository;
        this.userService = userService;
        this.dishRepository = dishRepository;
        this.dishEntityToDisplayDtoMapper = dishEntityToDisplayDtoMapper;
    }

    @Transactional
    public void createSearchFilter(final long ownerId) {
        userService.findUser(ownerId).setSearchFilter(searchFilterRepository.save(new SearchFilter()));
    }

    @Transactional
    public void createSearchFilterForFindRecipe(final long ownerId) {
        final SearchFilter searchFilter = new SearchFilter();
        searchFilter.setNeedGetRecipe(true);
        userService.findUser(ownerId).setSearchFilter(searchFilterRepository.save(searchFilter));
    }

    @Transactional
    public void deleteSearchFilter(final long ownerId) {
        final User user = userService.findUser(ownerId);
        final SearchFilter searchFilter = user.getSearchFilter();
        if (nonNull(searchFilter)) {
            user.setSearchFilter(null);
            searchFilterRepository.delete(searchFilter);
        }
    }

    @Transactional
    public void putDishType(final long ownerId, final DishType dishType) {
        userService.findUser(ownerId).getSearchFilter().setDishType(dishType);
    }

    @Transactional
    public void putSpicySign(final long ownerId, final boolean spicy) {
        userService.findUser(ownerId).getSearchFilter().setSpicy(spicy);
    }

    @Transactional
    public void putKitchen(final long ownerId, final WorldCuisine cuisine) {
        userService.findUser(ownerId).getSearchFilter().setCuisine(cuisine);
    }

    @Transactional
    public void putNeedPublicSearch(final long ownerId, final boolean searchFromPublicDish) {
        userService.findUser(ownerId).getSearchFilter().setSearchFromPublicDish(searchFromPublicDish);
    }

    @Transactional
    public Set<DisplayDishDto> searchDishWithCurrentFilter(final long ownerId) {
        final User currentUser = userService.findUser(ownerId);
        final SearchFilter searchFilter = currentUser.getSearchFilter();
        final Set<DisplayDishDto> result;
        final Function<Dish, DisplayDishDto> dishDisplayDtoFromEntityMapper = searchFilter.isNeedGetRecipe()
                ? dishEntityToDisplayDtoMapper::mapWithRecipe
                : dishEntityToDisplayDtoMapper::mapWithoutRecipe;
        if (searchFilter.isSearchFromPublicDish()) {
            result = findDishesWithSpecifiedFilter(searchFilter, currentUser)
                    .stream()
                    .map(dishDisplayDtoFromEntityMapper)
                    .collect(Collectors.toSet());
        } else {
            result = currentUser.getDishes().stream()
                    .filter(dish -> dishMatchesWithSpecifiedFilter(dish, searchFilter))
                    .filter(dish -> !searchFilter.isNeedGetRecipe() || nonNull(dish.getRecipe()))
                    .sorted(Comparator.comparing(Dish::getDishName))
                    .skip(currentUser.getSearchPageNumber() * 5L)
                    .limit(5)
                    .map(dishDisplayDtoFromEntityMapper)
                    .collect(Collectors.toSet());
        }
        currentUser.setSearchPageNumber(currentUser.getSearchPageNumber() + 1);
        return result;
    }

    private Set<Dish> findDishesWithSpecifiedFilter(final SearchFilter searchFilter, final User currentUser) {
        return searchFilter.isNeedGetRecipe()
                ? dishRepository.findAllDishByFilterWithSpecifiedOffsetAndRecipe(currentUser.getId(),
                searchFilter.getSpicy(),
                isNull(searchFilter.getDishType()) ? null : searchFilter.getDishType().name(),
                isNull(searchFilter.getCuisine()) ? null : searchFilter.getCuisine().name(),
                currentUser.getSearchPageNumber())
                : dishRepository.findAllDishByFilterWithSpecifiedOffset(currentUser.getId(),
                searchFilter.getSpicy(),
                isNull(searchFilter.getDishType()) ? null : searchFilter.getDishType().name(),
                isNull(searchFilter.getCuisine()) ? null : searchFilter.getCuisine().name(),
                currentUser.getSearchPageNumber());
    }

    public DisplayDishDto searchRandomDishWithCurrentFilter(final long ownerId) {
        final User currentUser = userService.findUser(ownerId);
        final SearchFilter searchFilter = currentUser.getSearchFilter();
        final Dish dish = searchFilter.isSearchFromPublicDish()
                ? getRandomPublishDishWithCurrentFilter(ownerId, searchFilter)
                : getRandomPrivateDishWithCurrentFilter(currentUser, searchFilter);
        return searchFilter.isNeedGetRecipe() ? dishEntityToDisplayDtoMapper.mapWithRecipe(dish) : dishEntityToDisplayDtoMapper.mapWithoutRecipe(dish);
    }

    private Dish getRandomPrivateDishWithCurrentFilter(final User currentUser, final SearchFilter searchFilter) {
        final Dish[] dishes = currentUser.getDishes().stream()
                .filter(d -> dishMatchesWithSpecifiedFilter(d, searchFilter))
                .filter(dish -> !searchFilter.isNeedGetRecipe() || nonNull(dish.getRecipe()))
                .toArray(Dish[]::new);
        if (dishes.length > 0) {
            return dishes[random.nextInt(dishes.length)];
        } else {
            throw new DishesNotFoundException();
        }
    }

    private Dish getRandomPublishDishWithCurrentFilter(final long ownerId, final SearchFilter searchFilter) {
        final int dishWithFilterCount = findDishWithFilterCount(ownerId, searchFilter);
        if (dishWithFilterCount > 0) {
            return findDishWithSpecifiedFilterAndOffset(ownerId, searchFilter, dishWithFilterCount);
        } else {
            throw new DishesNotFoundException();
        }
    }

    private Dish findDishWithSpecifiedFilterAndOffset(final long ownerId, final SearchFilter searchFilter, final int dishWithFilterCount) {
        return searchFilter.isNeedGetRecipe()
                ? dishRepository.findDishByFilterWithSpecifiedOffsetAndRecipe(ownerId, searchFilter.getSpicy(),
                isNull(searchFilter.getDishType()) ? null : searchFilter.getDishType().name(),
                isNull(searchFilter.getCuisine()) ? null : searchFilter.getCuisine().name(),
                random.nextInt(dishWithFilterCount))
                : dishRepository.findDishByFilterWithSpecifiedOffset(ownerId, searchFilter.getSpicy(),
                isNull(searchFilter.getDishType()) ? null : searchFilter.getDishType().name(),
                isNull(searchFilter.getCuisine()) ? null : searchFilter.getCuisine().name(),
                random.nextInt(dishWithFilterCount));
    }

    private int findDishWithFilterCount(final long ownerId, final SearchFilter searchFilter) {
        return searchFilter.isNeedGetRecipe()
                ? dishRepository.countDishWithFilterAndRecipe(ownerId, searchFilter.getSpicy(),
                isNull(searchFilter.getDishType()) ? null : searchFilter.getDishType().name(),
                isNull(searchFilter.getCuisine()) ? null : searchFilter.getCuisine().name())
                : dishRepository.countDishWithFilter(ownerId, searchFilter.getSpicy(),
                isNull(searchFilter.getDishType()) ? null : searchFilter.getDishType().name(),
                isNull(searchFilter.getCuisine()) ? null : searchFilter.getCuisine().name());
    }

    private boolean dishMatchesWithSpecifiedFilter(final Dish dish, final SearchFilter searchFilter) {
        return (isNull(searchFilter.getDishType()) || searchFilter.getDishType() == dish.getType())
                && (isNull(searchFilter.getSpicy()) || searchFilter.getSpicy() == dish.isSpicy())
                && (isNull(searchFilter.getCuisine()) || searchFilter.getCuisine() == dish.getCuisine());
    }
}
