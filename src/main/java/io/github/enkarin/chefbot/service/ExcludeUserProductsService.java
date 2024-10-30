package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.Product;
import io.github.enkarin.chefbot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiFunction;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExcludeUserProductsService {
    private final UserService userService;
    private final ProductRepository productRepository;

    public String[] findExcludeProducts(final long userId) {
        return userService.findUser(userId).getExcludeProducts().stream().map(Product::getProductName).toArray(String[]::new);
    }

    @Transactional
    public void addExcludeProducts(final long userId, final String... excludeProductNames) {
        final Set<Product> newExcludeProducts = new HashSet<>();
        for (final String excludeProductName : excludeProductNames) {
            newExcludeProducts.addAll(productRepository.findByProductNameContainsIgnoreCase(excludeProductName));
        }
        userService.findUser(userId).getExcludeProducts().addAll(newExcludeProducts);
    }

    @Transactional
    public void deleteExcludeProductsByEqualsNames(final long userId, final String... excludeProductNames) {
        deleteExcludeProducts(userId, excludeProductNames, (product, excludeProductName) -> product.getProductName().equalsIgnoreCase(excludeProductName));
    }

    @Transactional
    public void deleteExcludeProductsByLikeName(final long userId, final String... excludeProductNames) {
        deleteExcludeProducts(userId, excludeProductNames,
                (product, excludeProductName) -> product.getProductName().toLowerCase(Locale.ROOT).contains(excludeProductName.toLowerCase(Locale.ROOT)));
    }

    private void deleteExcludeProducts(final long userId, final String[] excludeProductNames, final BiFunction<Product, String, Boolean> comparisonCondition) {
        userService.findUser(userId).getExcludeProducts().removeIf(product -> {
            for (final String excludeProductName : excludeProductNames) {
                if (comparisonCondition.apply(product, excludeProductName)) {
                    return true;
                }
            }
            return false;
        });
    }
}
