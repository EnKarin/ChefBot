package io.github.enkarin.chefbot.entity;

import io.github.enkarin.chefbot.enums.WorldCuisine;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class SearchFilter implements Serializable {
    @Serial
    private static final long serialVersionUID = 7328055925213805550L;

    @Id
    @GeneratedValue
    private long id;

    private Boolean soup;
    private Boolean spicy;
    private boolean searchFromPublicDish;

    @Enumerated(EnumType.STRING)
    private WorldCuisine cuisine;
}
