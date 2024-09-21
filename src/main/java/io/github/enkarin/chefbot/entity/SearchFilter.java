package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.enums.DishType;
import io.github.enkarin.chefbot.enums.WorldCuisine;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
public class SearchFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 7328055925213805551L;

    @Id
    @GeneratedValue
    private long id;

    private boolean needGetRecipe;
    private Boolean spicy;
    private boolean searchFromPublicDish;
    private int pageNumber;

    @Enumerated(EnumType.STRING)
    private WorldCuisine cuisine;

    @Enumerated(EnumType.STRING)
    private DishType dishType;

    @OneToMany(mappedBy = "searchFilter", orphanRemoval = true)
    private List<SearchProduct> searchProductList;
}
