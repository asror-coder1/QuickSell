package com.quicksell.engine.auth.dto;

import com.quicksell.engine.billing.SubscriptionPlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String botToken,
        @NotBlank String botUsername,
        String domainName,
        String themeColor,
        SubscriptionPlan plan
) {
}
