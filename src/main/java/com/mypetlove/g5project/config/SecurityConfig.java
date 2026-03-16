package com.mypetlove.g5project.config;

import com.mypetlove.g5project.security.CustomUserDetailsService;
import com.mypetlove.g5project.security.CustomUserDetailsService;
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
                        .requestMatchers("/", "/login", "/register",
                                "/products", "/products/**",
                                "/services", "/services/**",
                                "/api/**",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        // Chỉ SHOP_OWNER
                        .requestMatchers("/shop/**", "/admin/**").hasRole("SHOP_OWNER")
                        // Chỉ CUSTOMER
                        .requestMatchers("/orders/**").hasRole("CUSTOMER")
                        // Cả 2 role đều cần login
                        .requestMatchers("/petlover/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
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
