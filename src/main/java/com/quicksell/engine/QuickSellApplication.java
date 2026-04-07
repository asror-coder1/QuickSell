package com.quicksell.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.quicksell.engine.billing.BillingProperties;
import com.quicksell.engine.security.JwtProperties;

@EnableCaching
@EnableScheduling
@EnableConfigurationProperties({JwtProperties.class, BillingProperties.class})
@SpringBootApplication
public class QuickSellApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickSellApplication.class, args);
    }
}
