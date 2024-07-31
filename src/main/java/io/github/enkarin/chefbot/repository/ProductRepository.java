package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
