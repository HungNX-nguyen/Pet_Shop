package com.mypetlove.g5project.security;

import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        // Tìm theo email hoặc username
        Account account = accountRepository.findByEmailOrUsername(input, input)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + input));

        // Kiểm tra tài khoản có bị khoá không
        if (!account.getIsActive()) {
            throw new UsernameNotFoundException("Tài khoản đã bị khoá!");
        }

        // Map roles từ DB sang GrantedAuthority cho Spring Security
        var authorities = account.getAccountRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().getRoleName()))
                .collect(Collectors.toList());

        return new User(
                account.getUsername(),
                account.getPassword(),
                authorities
        );
    }
}