package io.github.enkarin.chefbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -224945856393620757L;

    @Id
    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "moderator")
    private boolean moderator;

    @OneToMany(mappedBy = "owner")
    private Set<Dish> dishes;
}
