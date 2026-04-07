package com.quicksell.engine.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantRequestFilter extends OncePerRequestFilter {

    private static final String SHOP_HEADER = "X-Shop-Id";
    private static final String SHOP_PARAM = "shop_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional.ofNullable(request.getHeader(SHOP_HEADER))
                .or(() -> Optional.ofNullable(request.getParameter(SHOP_PARAM)))
                .filter(value -> !value.isBlank())
                .map(UUID::fromString)
                .ifPresent(TenantContext::setShopId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
