package com.quicksell.engine.billing;

import com.quicksell.engine.common.exception.ResourceNotFoundException;
import com.quicksell.engine.order.Order;
import com.quicksell.engine.order.OrderRepository;
import com.quicksell.engine.order.OrderStatus;
import com.quicksell.engine.order.PaymentType;
import com.quicksell.engine.payment.Payment;
import com.quicksell.engine.payment.PaymentProvider;
import com.quicksell.engine.payment.PaymentRepository;
import com.quicksell.engine.payment.PaymentStatus;
import com.quicksell.engine.shop.Shop;
import com.quicksell.engine.shop.ShopRepository;
import com.quicksell.engine.shop.ShopStatus;
import com.quicksell.engine.user.User;
import com.quicksell.engine.user.UserRepository;
import com.quicksell.engine.user.UserRole;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {

    private final BillingProperties billingProperties;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public BillingService(BillingProperties billingProperties,
                          ShopRepository shopRepository,
                          UserRepository userRepository,
                          PaymentRepository paymentRepository,
                          OrderRepository orderRepository) {
        this.billingProperties = billingProperties;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void applyPlatformFee(Shop shop, BigDecimal orderAmount) {
        BigDecimal fee = orderAmount
                .multiply(BigDecimal.valueOf(billingProperties.platformFeePercent()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        User seller = shop.getSeller();
        seller.setBalance(seller.getBalance().subtract(fee));

        userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .findFirst()
                .ifPresent(admin -> admin.setBalance(admin.getBalance().add(fee)));
    }

    @Transactional
    public Payment topUpBalance(UUID shopId, PaymentProvider provider, BigDecimal amount, String transactionId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        Payment payment = new Payment();
        payment.setTransactionId(transactionId);
        payment.setProvider(provider);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.SUCCESS);

        Order pseudoOrder = new Order();
        pseudoOrder.setShop(shop);
        pseudoOrder.setCustomerTgId(0L);
        pseudoOrder.setCustomerPhone("SYSTEM");
        pseudoOrder.setPaymentType(PaymentType.CARD);
        pseudoOrder.setStatus(OrderStatus.PAID);
        pseudoOrder.setTotalAmount(amount);
        pseudoOrder = orderRepository.save(pseudoOrder);
        payment.setOrder(pseudoOrder);

        shop.getSeller().setBalance(shop.getSeller().getBalance().add(amount));
        shop.setStatus(ShopStatus.ACTIVE);
        shop.setActive(true);
        shop.setExpiresAt(OffsetDateTime.now().plusDays(shop.getPlan() == SubscriptionPlan.FREE ? 14 : 30));
        return paymentRepository.save(payment);
    }

    @Transactional
    @Scheduled(cron = "0 */10 * * * *")
    public void expireOverdueShops() {
        List<Shop> overdueShops = shopRepository.findByExpiresAtBeforeAndStatusNot(OffsetDateTime.now(), ShopStatus.EXPIRED);
        overdueShops.forEach(shop -> {
            shop.setStatus(ShopStatus.EXPIRED);
            shop.setActive(false);
        });
    }
}
