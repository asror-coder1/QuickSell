package com.quicksell.engine.tenant;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_SHOP = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setShopId(UUID shopId) {
        CURRENT_SHOP.set(shopId);
    }

    public static UUID getShopId() {
        return CURRENT_SHOP.get();
    }

    public static void clear() {
        CURRENT_SHOP.remove();
    }
}
