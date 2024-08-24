package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.enums.WorldCuisine;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SearchFilter {
    @Id
    @GeneratedValue
    private long id;

    private Boolean soup;
    private Boolean spicy;
    private boolean searchFromPublicDish;

    @Enumerated(value = EnumType.STRING)
    private WorldCuisine cuisine;
}
