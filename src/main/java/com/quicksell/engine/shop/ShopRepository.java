package com.quicksell.engine.shop;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, UUID> {

    List<Shop> findByActiveTrueAndStatus(ShopStatus status);

    Optional<Shop> findByBotUsername(String botUsername);

    List<Shop> findByExpiresAtBeforeAndStatusNot(OffsetDateTime expiresAt, ShopStatus status);

    Optional<Shop> findFirstBySeller_Id(UUID sellerId);
}
