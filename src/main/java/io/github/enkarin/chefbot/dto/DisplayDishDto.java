package io.github.enkarin.chefbot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class DisplayDishDto {
    private final String dishName;
    private final Set<String> productsName;

    @Override
    public String toString() {
        return String.format("*%s:*\n-%s", dishName, String.join("\n-", productsName));
    }
}
