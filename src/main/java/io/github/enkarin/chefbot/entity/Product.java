package io.github.enkarin.chefbot.entity;

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
@Table(name = "t_product")
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = -5587244336804985464L;

    @Id
    private String productName;

    @OneToMany(mappedBy = "product")
    private Set<ProductQuantity> productQuantities;
}
