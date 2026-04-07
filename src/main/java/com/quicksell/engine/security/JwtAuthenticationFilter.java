package com.quicksell.engine.security;

import com.quicksell.engine.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SHOP_HEADER = "X-Shop-Id";
    private static final String SHOP_PARAM = "shop_id";
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional.ofNullable(request.getHeader(SHOP_HEADER))
                .or(() -> Optional.ofNullable(request.getParameter(SHOP_PARAM)))
                .filter(value -> !value.isBlank())
                .map(UUID::fromString)
                .ifPresent(TenantContext::setShopId);

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            if (header != null && header.startsWith("Bearer ")) {
                Claims claims = jwtService.parse(header.substring(7));
                UUID userId = UUID.fromString(claims.get("user_id", String.class));
                UUID shopId = Optional.ofNullable(claims.get("shop_id", String.class))
                        .filter(value -> !value.isBlank())
                        .map(UUID::fromString)
                        .orElse(null);

                AuthenticatedUser principal = new AuthenticatedUser(
                        userId,
                        shopId,
                        claims.getSubject(),
                        "",
                        Enum.valueOf(com.quicksell.engine.user.UserRole.class, claims.get("role", String.class))
                );

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (shopId != null) {
                    TenantContext.setShopId(shopId);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
