package com.quicksell.engine.catalog;

import com.quicksell.engine.billing.SubscriptionPlan;
import com.quicksell.engine.common.exception.BusinessException;
import com.quicksell.engine.common.exception.ResourceNotFoundException;
import com.quicksell.engine.shop.Shop;
import com.quicksell.engine.shop.ShopRepository;
import com.quicksell.engine.tma.dto.CategoryResponse;
import com.quicksell.engine.tma.dto.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;

    public CatalogService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ShopRepository shopRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.shopRepository = shopRepository;
    }

    @Cacheable(cacheNames = "shop-products", key = "#shopId")
    public List<ProductResponse> getProducts(UUID shopId) {
        return productRepository.findByShop_IdAndAvailableTrueOrderByCreatedAtDesc(shopId).stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getCategory() == null ? null : product.getCategory().getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getDiscountPrice(),
                        product.getStockQuantity(),
                        product.getImages()
                ))
                .toList();
    }

    @Cacheable(cacheNames = "shop-categories", key = "#shopId")
    public List<CategoryResponse> getCategories(UUID shopId) {
        return categoryRepository.findByShop_IdOrderBySortOrderAsc(shopId).stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getIconUrl(),
                        category.getSortOrder()
                ))
                .toList();
    }

    public void validateProductCreationLimit(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        long count = productRepository.countByShop_Id(shopId);
        if (count >= shop.getPlan().getProductLimit() && shop.getPlan() == SubscriptionPlan.FREE) {
            throw new BusinessException("FREE plan allows only 10 products");
        }
    }
}
