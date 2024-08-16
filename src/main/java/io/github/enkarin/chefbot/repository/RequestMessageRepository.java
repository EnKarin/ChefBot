package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.RequestMessageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestMessageRepository extends JpaRepository<RequestMessageInfo, Long> {
}
