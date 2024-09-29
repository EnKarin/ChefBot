package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.ProductQuantity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, Long> {
}
