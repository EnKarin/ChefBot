package io.github.enkarin.chefbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModerationRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6481956836432875498L;

    @Id
    @GeneratedValue
    @Column(name = "mr_id")
    private long id;

    @OneToOne
    @JoinColumn(name = "moderation_dish")
    private Dish moderationDish;

    @OneToMany(mappedBy = "currentModerationRequest", orphanRemoval = true)
    private List<ModerationRequestMessage> moderationRequestMessages;
}
