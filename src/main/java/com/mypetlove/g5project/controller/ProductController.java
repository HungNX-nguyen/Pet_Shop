package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.ProductRequest;
import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.service.CategoryService;
import com.mypetlove.g5project.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/products/create";
        }

        productService.create(request, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Tạo sản phẩm thành công");
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Product product = productService.getById(id);

        ProductRequest request = ProductRequest.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .isActive(product.getIsActive())
                .build();

        model.addAttribute("productId", id);
        model.addAttribute("productRequest", request);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/products/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute("productRequest") ProductRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/products/edit";
        }

        productService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        productService.toggleStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        return "redirect:/admin/products";
    }
}