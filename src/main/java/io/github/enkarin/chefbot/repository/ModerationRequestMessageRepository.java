package io.github.enkarin.chefbot.repository;

import io.github.enkarin.chefbot.entity.ModerationRequestMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationRequestMessageRepository extends JpaRepository<ModerationRequestMessage, Long> {
}
