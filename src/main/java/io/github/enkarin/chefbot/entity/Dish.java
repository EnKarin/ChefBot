package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@ToString
@Table(name = "t_dish")
public class Dish implements Serializable {
    @Serial
    private static final long serialVersionUID = -653331779227835564L;

    @Id
    @GeneratedValue
    @Column(name = "dish_id")
    private long id;

    @Column(name = "dish_name")
    private String dishName;

    @Column(name = "spicy")
    private boolean spicy;

    @Enumerated(EnumType.STRING)
    private DishType type;

    @Enumerated(EnumType.STRING)
    private WorldCuisine cuisine;

    @Column(name = "recipe", length = 2048)
    private String recipe;

    @ManyToMany
    @JoinTable(
            name = "t_dish_product",
            joinColumns = {@JoinColumn(name = "dish_id")},
            inverseJoinColumns = {@JoinColumn(name = "product_id")}
    )
    private Set<Product> products;

    @OneToOne(mappedBy = "moderationDish")
    private ModerationRequest moderationRequest;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(name = "published")
    private boolean published;
}
