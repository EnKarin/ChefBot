package io.github.enkarin.chefbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchProduct implements Serializable {
    @Serial
    private static final long serialVersionUID = 5632477786221956630L;

    @Id
    private String name;

    @ManyToOne
    @JoinColumn(name = "search_filter")
    private SearchFilter searchFilter;
}
