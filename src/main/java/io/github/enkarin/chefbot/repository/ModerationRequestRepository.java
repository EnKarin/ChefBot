package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.ModerationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationRequestRepository extends JpaRepository<ModerationRequest, Long> {
}
