package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.RegisterDto;
import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.AccountRole;
import com.mypetlove.g5project.entity.AccountRoleId;
import com.mypetlove.g5project.entity.Role;
import com.mypetlove.g5project.repository.AccountRepository;
import com.mypetlove.g5project.repository.AccountRoleRepository;
import com.mypetlove.g5project.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    // 1. Màn hình Đăng nhập
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    // 2. Màn hình Đăng ký
    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/register";
    }

    // 3. Xử lý Logic Đăng ký
    @Transactional
    @PostMapping("/register")
    public String processRegister(@ModelAttribute RegisterDto registerDto,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Bước 1: Kiểm tra mật khẩu nhập lại có khớp không
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "auth/register";
        }

        // Bước 2: Kiểm tra email đã tồn tại trong hệ thống chưa
        if (accountRepository.existsByEmail(registerDto.getEmail())) {
            model.addAttribute("error", "Email này đã được sử dụng!");
            return "auth/register";
        }

        // Bước 2.1: Kiểm tra username đã tồn tại trong hệ thống chưa
        if (accountRepository.existsByUsername(registerDto.getUsername())) {
            model.addAttribute("error", "Username này đã được sử dụng!");
            return "auth/register";
        }

        try {
            // Bước 3: Ánh xạ dữ liệu từ DTO sang Entity Account
            Account newAccount = Account.builder()
                    .fullName(registerDto.getFullName())
                    .email(registerDto.getEmail())
                    .username(registerDto.getUsername())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Bước 4: Lưu Account TRƯỚC để có ID
            accountRepository.save(newAccount);

            // Bước 5: Tìm Role CUSTOMER
            Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại!"));

            // Bước 6: Build composite key SAU KHI đã có accountId
            AccountRoleId accountRoleId = AccountRoleId.builder()
                    .accountId(newAccount.getAccountId())
                    .roleId(customerRole.getRoleId())
                    .build();

            // Bước 7: Build và lưu AccountRole
            AccountRole newAccountRole = AccountRole.builder()
                    .id(accountRoleId)
                    .account(newAccount)
                    .role(customerRole)
                    .build();
            accountRoleRepository.save(newAccountRole);

            // Bước 8: Thông báo thành công và chuyển hướng sang trang Login
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "auth/register";
        }
    }
}