package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "t_user")
public class User extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -224945856393620757L;

    @Column(name = "chat_id", nullable = false)
    private long chatId;

    @Column(name = "moderator", nullable = false)
    private boolean moderator;

    @OneToMany(mappedBy = "owner")
    private Set<Dish> dishes;
}
