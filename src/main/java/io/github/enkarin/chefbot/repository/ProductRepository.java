package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByProductNameContainsIgnoreCase(String name);
}
