package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
@Table(name = "t_product")
public class Product extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5587244336804985464L;

    @Column(name = "product_name")
    private String productName;

    @ManyToMany(mappedBy = "products")
    private Set<Dish> dishes;
}
