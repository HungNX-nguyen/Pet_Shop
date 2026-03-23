package com.mypetlove.g5project.config;

import com.mypetlove.g5project.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .userDetailsService(userDetailsService)
                .sessionManagement(session -> session
                        .sessionFixation().none()
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/register",
                                "/products", "/products/**",
                                "/css/**", "/js/**", "/images/**").permitAll()

                        // Đặt TRƯỚC rule /services/**
                        .requestMatchers("/services/*/booking").authenticated()

                        // Public cho services list/detail
                        .requestMatchers("/services", "/services/**").permitAll()

                        .requestMatchers("/bookings/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("SHOP_OWNER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            String contextPath = request.getContextPath();

                            SavedRequest savedRequest = requestCache.getRequest(request, response);

                            // ✅ Xóa savedRequest khỏi session ngay sau khi lấy
                            requestCache.removeRequest(request, response);

                            if (savedRequest != null
                                    && savedRequest.getRedirectUrl() != null
                                    && !savedRequest.getRedirectUrl().contains("/null")) { // ✅ filter URL rác
                                response.sendRedirect(savedRequest.getRedirectUrl());
                                return;
                            }

                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SHOP_OWNER"));

                            if (isAdmin) {
                                response.sendRedirect(contextPath + "/admin/services");
                            } else {
                                response.sendRedirect(contextPath + "/");
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}