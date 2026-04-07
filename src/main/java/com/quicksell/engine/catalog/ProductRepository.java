package com.quicksell.engine.catalog;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByShop_IdAndAvailableTrueOrderByCreatedAtDesc(UUID shopId);

    long countByShop_Id(UUID shopId);
}
