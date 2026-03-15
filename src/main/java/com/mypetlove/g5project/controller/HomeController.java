package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.DtoRespone.CategoryResponse;
import com.mypetlove.g5project.dto.DtoRespone.ProductCardResponse;
import com.mypetlove.g5project.dto.DtoRespone.ProductDetailResponse;
import com.mypetlove.g5project.service.CategoryService;
import com.mypetlove.g5project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String showHomepage(Model model) {
        List<CategoryResponse> categories = categoryService.getAllCategoryDtos();
        model.addAttribute("categories", categories);

        List<ProductCardResponse> bestSellers = productService.getBestSellerDtos();
        model.addAttribute("bestSellers", bestSellers);
        return "product/homepage";
    }

    @GetMapping("/products")
    public String showProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            Model model) {

        Pageable pageable = PageRequest.of(page, 9);
        Page<ProductCardResponse> productPage;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = categoryId != null;

        if (hasCategory && hasKeyword) {
            productPage = productService.searchProductDtosByCategory(categoryId, keyword.trim(), pageable);
        } else if (hasCategory) {
            productPage = productService.getProductDtosByCategory(categoryId, pageable);
        } else if (hasKeyword) {
            productPage = productService.searchProductDtos(keyword.trim(), pageable);
        } else {
            productPage = productService.getActiveProductDtos(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        List<CategoryResponse> categories = categoryService.getAllCategoryDtos();
        model.addAttribute("categories", categories);
        return "product/listproduct";
    }

    @GetMapping("/products/{id}")
    public String showProductDetail(@PathVariable Integer id, Model model) {
        ProductDetailResponse product = productService.getProductDetailById(id);
        model.addAttribute("product", product);

        // Related products: cùng category, loại trừ product hiện tại, lấy tối đa 4
        if (product.getCategoryId() != null) {
            List<ProductCardResponse> relatedProducts =
                    productService.getRelatedProducts(product.getCategoryId(), product.getId(), 4);
            model.addAttribute("relatedProducts", relatedProducts);
        }

        return "product/productdetail";
    }
}
