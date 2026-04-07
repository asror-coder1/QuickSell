package com.quicksell.engine.order;

import com.quicksell.engine.billing.BillingService;
import com.quicksell.engine.catalog.Product;
import com.quicksell.engine.catalog.ProductRepository;
import com.quicksell.engine.common.exception.BusinessException;
import com.quicksell.engine.common.exception.ResourceNotFoundException;
import com.quicksell.engine.shop.Shop;
import com.quicksell.engine.shop.ShopRepository;
import com.quicksell.engine.shop.ShopStatus;
import com.quicksell.engine.tma.dto.CreateOrderRequest;
import com.quicksell.engine.tma.dto.OrderResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final BillingService billingService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        ShopRepository shopRepository,
                        BillingService billingService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.billingService = billingService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        if (shop.getStatus() == ShopStatus.EXPIRED || shop.getExpiresAt().isBefore(java.time.OffsetDateTime.now())) {
            throw new BusinessException("Subscription expired. Payments required");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.productId()));
            if (!product.getShop().getId().equals(shop.getId())) {
                throw new BusinessException("Product does not belong to the selected shop");
            }
            if (product.getStockQuantity() < item.quantity()) {
                throw new BusinessException("Insufficient stock for " + product.getName());
            }

            BigDecimal unitPrice = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(item.quantity())));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(unitPrice);
            orderItems.add(orderItem);

            product.setStockQuantity(product.getStockQuantity() - item.quantity());
        }

        Order order = new Order();
        order.setShop(shop);
        order.setCustomerTgId(request.customerTgId());
        order.setCustomerPhone(request.customerPhone());
        order.setPaymentType(request.paymentType());
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(total);
        order = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        orderItemRepository.saveAll(orderItems);

        billingService.applyPlatformFee(shop, total);
        return new OrderResponse(order.getId(), total, order.getStatus());
    }
}
