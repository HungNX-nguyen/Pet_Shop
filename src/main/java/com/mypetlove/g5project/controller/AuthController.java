package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.RegisterDto;
import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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

        try {
            // Bước 3: Ánh xạ dữ liệu từ DTO sang Entity Account
            // BẢN CHẤT: Đảm bảo fullName không bị null để tránh lỗi MySQL
            Account newAccount = Account.builder()
                    .fullName(registerDto.getFullName()) // Lấy từ DTO
                    .email(registerDto.getEmail())       // Lấy từ DTO
                    .username(registerDto.getEmail())    // Tạm dùng email làm username (NOT NULL)
                    .password(passwordEncoder.encode(registerDto.getPassword())) // Băm mật khẩu
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Bước 4: Lưu xuống Database
            accountRepository.save(newAccount);

            // Bước 5: Thông báo thành công và chuyển hướng sang trang Login
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
            return "redirect:/login";

        } catch (Exception e) {
            // Xử lý lỗi phát sinh ngoài dự kiến (ví dụ lỗi DB)
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "auth/register";
        }
    }
}