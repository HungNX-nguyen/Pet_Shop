package com.mypetlove.g5project.controller.admin;

import com.mypetlove.g5project.entity.Order;
import com.mypetlove.g5project.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderManagementController {

    private final OrderManagementService orderManagementService;

    @GetMapping
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Model model
    ) {
        Page<Order> orderPage = orderManagementService.getOrders(keyword, status, page, size, sortBy, sortDir);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("orders", orderPage.getContent());

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("size", size);

        return "admin/orders/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Order order = orderManagementService.getOrderDetail(id);
        model.addAttribute("order", order);
        return "admin/orders/detail";
    }
}