package com.quicksell.engine.security;

import com.quicksell.engine.shop.ShopRepository;
import com.quicksell.engine.user.UserRepository;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    public AppUserDetailsService(UserRepository userRepository, ShopRepository shopRepository) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> {
                    UUID shopId = shopRepository.findFirstBySeller_Id(user.getId())
                            .map(shop -> shop.getId())
                            .orElse(null);
                    return new AuthenticatedUser(user.getId(), shopId, user.getEmail(), user.getPasswordHash(), user.getRole());
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
