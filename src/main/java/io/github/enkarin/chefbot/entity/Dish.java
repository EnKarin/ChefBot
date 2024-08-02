package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.enums.WorldCuisine;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "t_dish")
public class Dish implements Serializable {
    @Serial
    private static final long serialVersionUID = -653331779227835564L;

    @Id
    @Column(name = "dish_name")
    private String dishName;

    @Column(name = "spicy")
    private boolean spicy;

    @Column(name = "soup")
    private boolean soup;

    @Enumerated(EnumType.STRING)
    private WorldCuisine cuisine;

    @ManyToMany
    @JoinTable(
            name = "t_dish_product",
            joinColumns = {@JoinColumn(name = "dish_id")},
            inverseJoinColumns = {@JoinColumn(name = "product_id")}
    )
    private Set<Product> products;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(name = "published", nullable = false)
    private boolean published;
}
