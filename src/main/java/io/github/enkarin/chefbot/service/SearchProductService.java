package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.SearchFilter;
import io.github.enkarin.chefbot.entity.SearchProduct;
import io.github.enkarin.chefbot.repository.SearchProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SearchProductService {
    private final SearchProductRepository searchProductRepository;
    private final UserService userService;

    @Transactional
    public void saveProductsForCurrentSearchFilter(final long userId, final String... productsName) {
        final SearchFilter searchFilter = userService.findUser(userId).getSearchFilter();
        for (final String productName : productsName) {
            searchProductRepository.save(new SearchProduct(StringUtils.capitalize(productName.trim().toLowerCase(Locale.ROOT)), searchFilter));
        }
    }
}
