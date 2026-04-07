package com.quicksell.engine.tma.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String iconUrl,
        Integer sortOrder
) {
}
