package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/customers")
public class AdminAccountController {

    private final AccountRepository accountRepository;

    @GetMapping
    public String listCustomers(@RequestParam(value = "status", required = false) String status,
                                @RequestParam(value = "role", required = false) String role,
                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                Model model) {
        // Danh sách toàn bộ account trong hệ thống (dùng cho thống kê tổng)
        var allAccounts = accountRepository.findAll();

        // Danh sách sẽ hiển thị (sau khi áp dụng filter)
        var accounts = allAccounts;

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            boolean active = "ACTIVE".equalsIgnoreCase(status);
            accounts = accounts.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsActive()) == active)
                    .toList();
        }

        if (role != null && !role.isBlank() && !"ALL".equalsIgnoreCase(role)) {
            String roleUpper = role.toUpperCase();
            accounts = accounts.stream()
                    .filter(a -> a.getAccountRoles() != null &&
                            a.getAccountRoles().stream()
                                    .anyMatch(ar -> ar.getRole() != null &&
                                            roleUpper.equalsIgnoreCase(ar.getRole().getRoleName())))
                    .toList();
        }

        // Thống kê tổng không phụ thuộc filter
        long totalUsers = allAccounts.size();
        long activeUsers = allAccounts.stream().filter(a -> Boolean.TRUE.equals(a.getIsActive())).count();

        // Pagination 10 bản ghi / trang cho danh sách đã lọc
        int pageSize = 10;
        int totalFiltered = accounts.size();
        int totalPages = (int) Math.ceil((double) totalFiltered / pageSize);
        if (page < 1) page = 1;
        if (totalPages == 0) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalFiltered);
        var pageAccounts = totalFiltered == 0 ? accounts : accounts.subList(fromIndex, toIndex);

        int fromDisplay = totalFiltered == 0 ? 0 : fromIndex + 1;
        int toDisplay = totalFiltered == 0 ? 0 : toIndex;
        int prevPage = page > 1 ? page - 1 : 1;
        int nextPage = page < totalPages ? page + 1 : page;

        model.addAttribute("accounts", pageAccounts);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("totalFiltered", totalFiltered);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("fromDisplay", fromDisplay);
        model.addAttribute("toDisplay", toDisplay);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("statusFilter", status == null ? "ALL" : status.toUpperCase());
        model.addAttribute("roleFilter", role == null ? "ALL" : role.toUpperCase());

        return "admin/userlist";
    }

    @GetMapping("/{id}")
    public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        return accountRepository.findById(id)
                .map(account -> {
                    model.addAttribute("account", account);
                    return "admin/user-detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Account không tồn tại");
                    return "redirect:/admin/customers";
                });
    }

    @GetMapping("/{id}/edit")
    public String editCustomerForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        return accountRepository.findById(id)
                .map(account -> {
                    model.addAttribute("account", account);
                    return "admin/user-edit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Account không tồn tại");
                    return "redirect:/admin/customers";
                });
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        return accountRepository.findById(id)
                .map(account -> {
                    account.setIsActive(account.getIsActive() == null ? Boolean.FALSE : !account.getIsActive());
                    accountRepository.save(account);
                    redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái tài khoản thành công");
                    return "redirect:/admin/customers";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Account không tồn tại");
                    return "redirect:/admin/customers";
                });
    }
}

