package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.enums.ChatStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
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
    @Column(name = "user_id")
    private long id;

    @Column(name = "chat_id", unique = true)
    private long chatId;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "moderator")
    private boolean moderator;

    @OneToOne
    @JoinColumn(name = "editable_dish_id")
    private Dish editabledDish;

    @OneToMany(mappedBy = "owner")
    private Set<Dish> dishes;

    @Enumerated(EnumType.STRING)
    private ChatStatus chatStatus;

    @Enumerated(EnumType.STRING)
    private ChatStatus previousChatStatus;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "search_filter_id")
    private SearchFilter searchFilter;

    @ManyToOne
    @JoinColumn(name = "moderate_dish")
    private Dish moderableDish;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<SearchProduct> searchProductList;

    private int searchPageNumber;
}
