package io.github.enkarin.chefbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModerationRequestMessage {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "message_id")
    private int messageId;

    private long chatId;

    @ManyToOne
    @JoinColumn(name = "current_moderation_request")
    private ModerationRequest currentModerationRequest;
}
