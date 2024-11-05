package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.SearchProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchProductRepository extends JpaRepository<SearchProduct, Long> {
}
