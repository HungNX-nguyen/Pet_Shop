package com.mypetlove.g5project.config;

import com.mypetlove.g5project.security.CustomUserDetailsService;
import com.mypetlove.g5project.service.CartService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CartService cartService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(authorize -> authorize
                        // Public - ai cũng vào được
                        .requestMatchers(
                                "/", "/login", "/register",
                                "/products", "/products/**",
                                "/services", "/services/**",
                                "/cart", "/api/cart/**",
                                "/css/**", "/js/**", "/images/**",
                                "/payment/vnpay-return"    // ✅ THÊM DÒNG NÀY
                        ).permitAll()
                        // Chỉ SHOP_OWNER
                        .requestMatchers("/shop/**", "/admin/**").hasRole("SHOP_OWNER")
                        // Chỉ CUSTOMER
                        .requestMatchers("/orders/**").hasRole("CUSTOMER")
                        .requestMatchers("/orders/*/checkout", "/orders/*/confirm").hasRole("CUSTOMER")
                        // ✅ THÊM — cho phép CUSTOMER tạo payment VNPay
                        .requestMatchers("/payment/vnpay/create/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/orders/**").authenticated()
                        // Cả 2 role đều cần login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            // Merge cart from cookie to database
                            if (request.getCookies() != null) {
                                for (Cookie cookie : request.getCookies()) {
                                    if ("pet_cart".equals(cookie.getName())) {
                                        try {
                                            String cookieValue = java.net.URLDecoder.decode(cookie.getValue(), java.nio.charset.StandardCharsets.UTF_8);
                                            cartService.mergeCart(cookieValue, authentication.getName());
                                        } catch (Exception e) {
                                            // Ignore decoding errors
                                        }
                                        // Clear the cookie
                                        Cookie clearCookie = new Cookie("pet_cart", "");
                                        clearCookie.setPath("/");
                                        clearCookie.setMaxAge(0);
                                        response.addCookie(clearCookie);
                                        break;
                                    }
                                }
                            }

                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .anyMatch(auth -> auth.equals("ROLE_SHOP_OWNER"));

                            String contextPath = request.getContextPath();
                            if (isAdmin) {
                                response.sendRedirect(contextPath + "/admin/customers");
                            } else {
                                response.sendRedirect(contextPath + "/");
                            }
                        })
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
