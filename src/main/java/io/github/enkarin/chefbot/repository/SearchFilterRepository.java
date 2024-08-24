package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.SearchFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchFilterRepository extends JpaRepository<SearchFilter, Long> {
}
