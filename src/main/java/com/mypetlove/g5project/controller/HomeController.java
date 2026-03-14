package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.entity.Category;
import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.service.CategoryService;
import com.mypetlove.g5project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String showHomepage(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        List<Product> bestSeller = productService.getBestSellers();
        model.addAttribute("bestSellers", bestSeller);
        return "product/homepage";
    }

    @GetMapping("/products")
    public String showProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            Model model) {

        Pageable pageable = PageRequest.of(page, 9);
        Page<Product> productPage;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = categoryId != null;

        if (hasCategory && hasKeyword) {
            productPage = productService.searchProductsByCategory(categoryId, keyword.trim(), pageable);
        } else if (hasCategory) {
            productPage = productService.getProductsByCategory(categoryId, pageable);
        } else if (hasKeyword) {
            productPage = productService.searchProducts(keyword.trim(), pageable);
        } else {
            productPage = productService.getActiveProducts(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "product/listproduct";
    }
}


