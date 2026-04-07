package com.quicksell.engine.payment;

import com.quicksell.engine.billing.BillingService;
import com.quicksell.engine.payment.dto.BalanceTopUpRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final BillingService billingService;

    public PaymentController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/top-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Payment topUp(@Valid @RequestBody BalanceTopUpRequest request) {
        return billingService.topUpBalance(
                request.shopId(),
                request.provider(),
                request.amount(),
                request.transactionId()
        );
    }
}
