package com.quicksell.engine.tma.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal discountPrice,
        Integer stockQuantity,
        List<String> images
) {
}
