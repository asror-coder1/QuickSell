package com.quicksell.engine.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        UUID userId,
        UUID shopId
) {
}
