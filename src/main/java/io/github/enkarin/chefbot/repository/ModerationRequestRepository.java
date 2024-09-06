package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.ModerationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationRequestRepository extends JpaRepository<ModerationRequest, Long> {
    List<ModerationRequest> findByFreshIsTrue();
    List<ModerationRequest> findByDeclineCauseIsNotNull();
    List<ModerationRequest> findByDeclineCauseIsNull();
}
