package com.quicksell.engine.auth;

import com.quicksell.engine.auth.dto.AuthResponse;
import com.quicksell.engine.auth.dto.LoginRequest;
import com.quicksell.engine.auth.dto.RegisterRequest;
import com.quicksell.engine.billing.SubscriptionPlan;
import com.quicksell.engine.common.exception.ResourceNotFoundException;
import com.quicksell.engine.security.JwtService;
import com.quicksell.engine.shop.Shop;
import com.quicksell.engine.shop.ShopRepository;
import com.quicksell.engine.shop.ShopStatus;
import com.quicksell.engine.user.User;
import com.quicksell.engine.user.UserRepository;
import com.quicksell.engine.user.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       ShopRepository shopRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.SELLER);
        user = userRepository.save(user);

        Shop shop = new Shop();
        shop.setSeller(user);
        shop.setBotToken(request.botToken());
        shop.setBotUsername(request.botUsername());
        shop.setDomainName(request.domainName());
        shop.setThemeColor(request.themeColor());
        shop.setPlan(request.plan() == null ? SubscriptionPlan.FREE : request.plan());
        shop.setStatus(ShopStatus.ACTIVE);
        shop.setExpiresAt(OffsetDateTime.now().plusDays(shop.getPlan() == SubscriptionPlan.FREE ? 14 : 30));
        shop = shopRepository.save(shop);

        return new AuthResponse(jwtService.generateToken(user, shop.getId()), user.getId(), shop.getId());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UUID shopId = request.shopId() == null || request.shopId().isBlank()
                ? shopRepository.findFirstBySeller_Id(user.getId()).map(Shop::getId).orElse(null)
                : UUID.fromString(request.shopId());

        return new AuthResponse(jwtService.generateToken(user, shopId), user.getId(), shopId);
    }
}
