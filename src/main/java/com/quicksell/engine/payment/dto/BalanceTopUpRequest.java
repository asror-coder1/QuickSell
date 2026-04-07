package com.quicksell.engine.payment.dto;

import com.quicksell.engine.payment.PaymentProvider;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record BalanceTopUpRequest(
        @NotNull UUID shopId,
        @NotNull PaymentProvider provider,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String transactionId
) {
}
