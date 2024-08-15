package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.enums.ModerationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class ModerationRequest {
    @Id
    @GeneratedValue
    @Column(name = "mq_id")
    private long id;

    @Enumerated
    private ModerationStatus status;

    @OneToOne
    @JoinColumn(name = "from_user")
    private User fromUser;

    @OneToOne
    @JoinColumn(name = "moderation_dish")
    private Dish moderationDish;

    @OneToMany(mappedBy = "currentModerationRequest", orphanRemoval = true)
    private Set<RequestMessage> moderatorsRequestMessages;
}
