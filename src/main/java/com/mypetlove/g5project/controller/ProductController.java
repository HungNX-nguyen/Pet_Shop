package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.ProductRequest;
import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.service.CategoryService;
import com.mypetlove.g5project.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<Product> productPage = productService.getAllProducts(keyword, categoryId, isActive, sort, page, size);

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("isActive", isActive);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);
        return "admin/products/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/products/create";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("productRequest") ProductRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/products/create";
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá phải lớn hơn 0");
        }

        if (request.getStockQuantity() == null || request.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng tồn không được âm");
        }

        try {
            String username = (authentication != null) ? authentication.getName() : null;
            productService.create(request, username);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo sản phẩm thành công");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/products/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, HttpServletRequest request) {
        Product product = productService.getById(id);

        ProductRequest form = ProductRequest.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .isActive(product.getIsActive())
                .build();

        model.addAttribute("productId", id);
        model.addAttribute("productRequest", form);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("ctx", request.getContextPath());
        return "admin/products/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute("productRequest") ProductRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpServletRequest httpRequest
    ) {
        if (bindingResult.hasErrors()) {
            if (request.getImageUrl() == null || request.getImageUrl().isBlank()) {
                Product product = productService.getById(id);
                request.setImageUrl(product.getImageUrl());
            }
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ctx", httpRequest.getContextPath());
            return "admin/products/edit";
        }

        try {
            productService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công");
            return "redirect:/admin/products";
        } catch (Exception e) {
            if (request.getImageUrl() == null || request.getImageUrl().isBlank()) {
                Product product = productService.getById(id);
                request.setImageUrl(product.getImageUrl());
            }
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ctx", httpRequest.getContextPath());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/products/edit";
        }
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        productService.toggleStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        return "redirect:/admin/products";
    }
}