package com.quicksell.engine.billing;

public enum SubscriptionPlan {
    FREE(10, false, false),
    PRO(Integer.MAX_VALUE, true, false),
    BUSINESS(Integer.MAX_VALUE, true, true);

    private final int productLimit;
    private final boolean analyticsEnabled;
    private final boolean brandingEnabled;

    SubscriptionPlan(int productLimit, boolean analyticsEnabled, boolean brandingEnabled) {
        this.productLimit = productLimit;
        this.analyticsEnabled = analyticsEnabled;
        this.brandingEnabled = brandingEnabled;
    }

    public int getProductLimit() {
        return productLimit;
    }

    public boolean isAnalyticsEnabled() {
        return analyticsEnabled;
    }

    public boolean isBrandingEnabled() {
        return brandingEnabled;
    }
}
