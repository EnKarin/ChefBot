package io.github.enkarin.chefbot.service;

import io.github.enkarin.chefbot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcludeUserProductsService {
    private final UserService userService;
    private final ProductRepository productRepository;


}
