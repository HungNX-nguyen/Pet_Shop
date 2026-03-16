package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.CartDetailDto;
import com.mypetlove.g5project.service.CartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String showCartDetail(Model model, Authentication authentication, HttpServletRequest request) {
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());

        CartDetailDto cart;
        if (isLoggedIn) {
            cart = cartService.getCartDetails(authentication.getName());
        } else {
            String cookieJson = getCartCookieValue(request);
            cart = cartService.getCartDetailsFromCookie(cookieJson);
        }
        model.addAttribute("cart", cart);
        return "cart/cartdetail";
    }

    private String getCartCookieValue(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("pet_cart".equals(cookie.getName())) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        return "[]";
                    }
                }
            }
        }
        return "[]";
    }
}
