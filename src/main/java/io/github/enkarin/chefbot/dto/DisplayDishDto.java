package io.github.enkarin.chefbot.dto;

import java.util.Set;

public record DisplayDishDto(String dishName, Set<String> productsName) {
    @Override
    public String toString() {
        return String.format("*%s:*\n-%s", dishName, String.join("\n-", productsName));
    }
}
