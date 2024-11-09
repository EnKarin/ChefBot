package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.dto.DisplayDishDto;
import io.github.enkarin.chefbot.entity.Dish;
import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.entity.ProductQuantity;
import io.github.enkarin.chefbot.entity.SearchProduct;
import io.github.enkarin.chefbot.entity.User;
import io.github.enkarin.chefbot.mappers.DishEntityToDisplayDtoMapper;
import io.github.enkarin.chefbot.repository.ProductRepository;
import io.github.enkarin.chefbot.repository.SearchProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchProductService {
    private final SearchProductRepository searchProductRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final DishEntityToDisplayDtoMapper dishEntityToDisplayDtoMapper;

    public void dropSearchProductForUser(final long userId) {
        searchProductRepository.deleteAll(userService.findUser(userId).getSearchProductList());
    }

    public void saveProductsForCurrentSearchFilter(final long userId, final String... productsName) {
        final User user = userService.findUser(userId);
        for (final String productName : productsName) {
            searchProductRepository.save(SearchProduct.builder().name(StringUtils.capitalize(productName.trim().toLowerCase(Locale.ROOT))).user(user).build());
        }
    }

    public List<? extends DisplayDishDto> findDishByProduct(final long userId) {
        final User user = userService.findUser(userId);
        final List<? extends DisplayDishDto> result = findDishByProduct(user, user.getSearchProductList().stream().map(SearchProduct::getName).toList(), user.getSearchPageNumber());
        user.setSearchPageNumber(user.getSearchPageNumber() + 1);
        return result;
    }

    private List<? extends DisplayDishDto> findDishByProduct(final User user, final List<String> productNames, final int pageNumber) {
        final Iterator<String> productIterator = productNames.iterator();
        Set<Dish> possibleResult = productRepository.findByProductNameContainsIgnoreCase(productIterator.next()).stream()
                .flatMap(product -> product.getProductQuantities().stream())
                .map(ProductQuantity::getDish)
                .filter(dish -> dish.isPublished() || dish.getOwner().equals(user))
                .collect(Collectors.toSet());
        while (productIterator.hasNext()) {
            final String nowProductName = productIterator.next().toLowerCase(Locale.ROOT);
            possibleResult = possibleResult.stream()
                    .filter(dish -> dish.getProducts().stream()
                            .map(ProductQuantity::getProduct)
                            .map(Product::getProductName)
                            .map(String::toLowerCase)
                            .anyMatch(productName -> productName.contains(nowProductName)))
                    .collect(Collectors.toSet());
        }
        return possibleResult.stream()
                .filter(dish -> userService.dishNotContainExcludeProduct(dish, user))
                .sorted(Comparator.comparing(Dish::getDishName))
                .skip(pageNumber * 5L)
                .limit(5)
                .map(dish -> nonNull(dish.getRecipe()) ? dishEntityToDisplayDtoMapper.mapWithRecipe(dish) : dishEntityToDisplayDtoMapper.mapWithoutRecipe(dish))
                .toList();
    }
}
