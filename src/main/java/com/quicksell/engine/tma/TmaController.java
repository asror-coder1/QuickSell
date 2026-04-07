package com.quicksell.engine.tma;

import com.quicksell.engine.catalog.CatalogService;
import com.quicksell.engine.order.OrderService;
import com.quicksell.engine.tma.dto.CategoryResponse;
import com.quicksell.engine.tma.dto.CreateOrderRequest;
import com.quicksell.engine.tma.dto.OrderResponse;
import com.quicksell.engine.tma.dto.ProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tma")
public class TmaController {

    private final CatalogService catalogService;
    private final OrderService orderService;

    public TmaController(CatalogService catalogService, OrderService orderService) {
        this.catalogService = catalogService;
        this.orderService = orderService;
    }

    @GetMapping("/products")
    public List<ProductResponse> products(@RequestParam("shop_id") UUID shopId) {
        return catalogService.getProducts(shopId);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories(@RequestParam("shop_id") UUID shopId) {
        return catalogService.getCategories(shopId);
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
