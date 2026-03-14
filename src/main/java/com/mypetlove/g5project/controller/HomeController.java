package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.entity.Category;
import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.service.CategoryService;
import com.mypetlove.g5project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
