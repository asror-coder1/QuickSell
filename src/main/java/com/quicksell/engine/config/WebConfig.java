package com.quicksell.engine.config;

import com.quicksell.engine.tenant.TenantHibernateInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TenantHibernateInterceptor tenantHibernateInterceptor;

    public WebConfig(TenantHibernateInterceptor tenantHibernateInterceptor) {
        this.tenantHibernateInterceptor = tenantHibernateInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantHibernateInterceptor);
    }
}
