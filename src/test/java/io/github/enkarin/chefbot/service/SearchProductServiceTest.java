package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.entity.SearchProduct;
import io.github.enkarin.chefbot.enums.ChatStatus;
import io.github.enkarin.chefbot.repository.SearchProductRepository;
import io.github.enkarin.chefbot.util.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class SearchProductServiceTest extends TestBase {
    @Autowired
    private SearchProductService searchProductService;

    @Autowired
    private SearchProductRepository searchProductRepository;

    @Test
    void saveProductsForCurrentSearchFilter() {
        createUser(ChatStatus.ENRICHING_RECIPES);

        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "Три ведра укропа", "Ведро воды");

        assertThat(searchProductRepository.findAll()).extracting(SearchProduct::getName).containsOnly("Три ведра укропа", "Ведро воды");
    }

    @Test
    void saveProductsForCurrentSearchFilterMustParseInput() {
        createUser(ChatStatus.ENRICHING_RECIPES);

        searchProductService.saveProductsForCurrentSearchFilter(USER_ID, "три ведра укропа", "ведро Воды");

        assertThat(searchProductRepository.findAll()).extracting(SearchProduct::getName).containsOnly("Три ведра укропа", "Ведро воды");
    }
}