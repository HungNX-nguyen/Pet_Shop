package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.AddToCartDto;
import com.mypetlove.g5project.dto.CartResponseDto;
import com.mypetlove.g5project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

    private boolean isGuest(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName());
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> addToCart(@RequestBody AddToCartDto dto, Authentication authentication) {
        if (isGuest(authentication)) {
            return ResponseEntity.ok(
                    CartResponseDto.builder()
                            .status("success")
                            .message("Đã thêm vào giỏ hàng")
                            .totalItems(0)
                            .build()
            );
        }

        String username = authentication.getName();
        try {
            CartResponseDto response = cartService.addToCart(dto, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CartResponseDto.builder()
                            .status("error")
                            .message(e.getMessage())
                            .totalItems(0)
                            .build()
            );
        }
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateCartItem(
            @PathVariable("cartItemId") Integer cartItemId,
            @RequestParam("quantity") Integer quantity,
            Authentication authentication) {
        if (isGuest(authentication)) {
            return ResponseEntity.ok(CartResponseDto.builder().status("success").message("OK").totalItems(0).build());
        }

        try {
            CartResponseDto response = cartService.updateCartItem(cartItemId, quantity, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CartResponseDto.builder()
                    .status("error")
                    .message(e.getMessage())
                    .totalItems(0)
                    .build());
        }
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeCartItem(
            @PathVariable("cartItemId") Integer cartItemId,
            Authentication authentication) {
        if (isGuest(authentication)) {
            return ResponseEntity.ok(CartResponseDto.builder().status("success").message("OK").totalItems(0).build());
        }

        try {
            CartResponseDto response = cartService.removeCartItem(cartItemId, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CartResponseDto.builder()
                    .status("error")
                    .message(e.getMessage())
                    .totalItems(0)
                    .build());
        }
    }

    @PutMapping("/update-product")
    public ResponseEntity<CartResponseDto> updateByProduct(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity,
            Authentication authentication) {
        if (isGuest(authentication)) {
            return ResponseEntity.ok(CartResponseDto.builder().status("success").message("OK").totalItems(0).build());
        }

        try {
            CartResponseDto response = cartService.updateCartItemByProduct(productId, quantity, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CartResponseDto.builder()
                    .status("error")
                    .message(e.getMessage())
                    .totalItems(0)
                    .build());
        }
    }

    @DeleteMapping("/remove-product/{productId}")
    public ResponseEntity<CartResponseDto> removeByProduct(
            @PathVariable("productId") Integer productId,
            Authentication authentication) {
        if (isGuest(authentication)) {
            return ResponseEntity.ok(CartResponseDto.builder().status("success").message("OK").totalItems(0).build());
        }

        try {
            CartResponseDto response = cartService.removeCartItemByProduct(productId, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CartResponseDto.builder()
                    .status("error")
                    .message(e.getMessage())
                    .totalItems(0)
                    .build());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<CartResponseDto> getCartCount(Authentication authentication) {
        if (isGuest(authentication)) {
            return ResponseEntity.ok(CartResponseDto.builder().status("success").message("OK").totalItems(0).build());
        }

        try {
            int totalItems = cartService.getCartTotalItems(authentication.getName());
            return ResponseEntity.ok(CartResponseDto.builder()
                    .status("success")
                    .message("OK")
                    .totalItems(totalItems)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CartResponseDto.builder()
                    .status("error")
                    .message(e.getMessage())
                    .totalItems(0)
                    .build());
        }
    }
}
