package com.mypetlove.g5project.service;

import com.mypetlove.g5project.dto.AddToCartDto;
import com.mypetlove.g5project.dto.CartDetailDto;
import com.mypetlove.g5project.dto.CartResponseDto;

public interface CartService {
    CartResponseDto addToCart(AddToCartDto dto, String username);
    CartDetailDto getCartDetails(String username);
    CartResponseDto updateCartItem(Integer cartItemId, Integer quantity, String username);
    CartResponseDto removeCartItem(Integer cartItemId, String username);
    CartResponseDto updateCartItemByProduct(Integer productId, Integer quantity, String username);
    CartResponseDto removeCartItemByProduct(Integer productId, String username);
    CartDetailDto getCartDetailsFromCookie(String cookieJson);
}
