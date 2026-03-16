package com.mypetlove.g5project.service.impl;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.mypetlove.g5project.dto.AddToCartDto;
import com.mypetlove.g5project.dto.CartDetailDto;
import com.mypetlove.g5project.dto.CartItemDto;
import com.mypetlove.g5project.dto.CartResponseDto;
import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.Cart;
import com.mypetlove.g5project.entity.CartItem;
import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.repository.AccountRepository;
import com.mypetlove.g5project.repository.CartItemRepository;
import com.mypetlove.g5project.repository.CartRepository;
import com.mypetlove.g5project.repository.ProductRepository;
import com.mypetlove.g5project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public CartResponseDto addToCart(AddToCartDto dto, String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByCustomer_Username(username)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(account);
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + dto.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(dto.getQuantity());
        }
        cartItemRepository.save(cartItem);

        int totalItems = getCartTotalItems(cart.getId());

        return CartResponseDto.builder()
                .status("success")
                .message("Đã thêm vào giỏ hàng")
                .totalItems(totalItems)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CartDetailDto getCartDetails(String username) {
        Cart cart = cartRepository.findByCustomer_Username(username).orElse(null);
        if (cart == null || cart.getCartItems().isEmpty()) {
            return buildEmptyCartDetail();
        }

        List<CartItemDto> items = cart.getCartItems().stream()
                .map(item -> CartItemDto.builder()
                        .cartItemId(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productCategory(item.getProduct().getCategory() != null ? item.getProduct().getCategory().getName() : "Uncategorized")
                        .imageUrl(item.getProduct().getImageUrl())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return buildCartDetail(items);
    }

    @Override
    @Transactional
    public CartResponseDto updateCartItem(Integer cartItemId, Integer quantity, String username) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        int totalItems = getCartTotalItems(cartItem.getCart().getId());

        return CartResponseDto.builder()
                .status("success")
                .message("Cập nhật giỏ hàng thành công")
                .totalItems(totalItems)
                .build();
    }

    @Override
    @Transactional
    public CartResponseDto removeCartItem(Integer cartItemId, String username) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        Integer cartId = cartItem.getCart().getId();
        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();
        int totalItems = getCartTotalItems(cartId);

        return CartResponseDto.builder()
                .status("success")
                .message("Xóa sản phẩm thành công")
                .totalItems(totalItems)
                .build();
    }

    @Override
    @Transactional
    public CartResponseDto updateCartItemByProduct(Integer productId, Integer quantity, String username) {
        CartItem cartItem = cartItemRepository.findByCartCustomerUsernameAndProductId(username, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Integer cartId = cartItem.getCart().getId();

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            cartItemRepository.flush();
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        int totalItems = getCartTotalItems(cartId);

        return CartResponseDto.builder()
                .status("success")
                .message("Cập nhật giỏ hàng thành công")
                .totalItems(totalItems)
                .build();
    }

    @Override
    @Transactional
    public CartResponseDto removeCartItemByProduct(Integer productId, String username) {
        CartItem cartItem = cartItemRepository.findByCartCustomerUsernameAndProductId(username, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Integer cartId = cartItem.getCart().getId();
        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();
        int totalItems = getCartTotalItems(cartId);

        return CartResponseDto.builder()
                .status("success")
                .message("Xóa sản phẩm thành công")
                .totalItems(totalItems)
                .build();
    }

    @Override
    public CartDetailDto getCartDetailsFromCookie(String cookieJson) {
        if (cookieJson == null || cookieJson.isBlank()) {
            return buildEmptyCartDetail();
        }

        List<AddToCartDto> cookieItems;
        try {
            cookieItems = objectMapper.readValue(cookieJson, new TypeReference<List<AddToCartDto>>() {});
        } catch (Exception e) {
            return buildEmptyCartDetail();
        }

        if (cookieItems == null || cookieItems.isEmpty()) {
            return buildEmptyCartDetail();
        }

        List<Integer> productIds = cookieItems.stream()
                .map(AddToCartDto::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItemDto> items = cookieItems.stream()
                .filter(ci -> productMap.containsKey(ci.getProductId()))
                .map(ci -> {
                    Product p = productMap.get(ci.getProductId());
                    return CartItemDto.builder()
                            .cartItemId(null)
                            .productId(p.getId())
                            .productName(p.getName())
                            .productCategory(p.getCategory() != null ? p.getCategory().getName() : "Uncategorized")
                            .imageUrl(p.getImageUrl())
                            .price(p.getPrice())
                            .quantity(ci.getQuantity())
                            .totalPrice(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                            .build();
                })
                .collect(Collectors.toList());

        return buildCartDetail(items);
    }

    private CartDetailDto buildEmptyCartDetail() {
        return CartDetailDto.builder()
                .items(new ArrayList<>())
                .subtotal(BigDecimal.ZERO)
                .shipping(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
    }

    private CartDetailDto buildCartDetail(List<CartItemDto> items) {
        if (items.isEmpty()) {
            return buildEmptyCartDetail();
        }

        BigDecimal subtotal = items.stream()
                .map(CartItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shipping = subtotal.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("5.99") : BigDecimal.ZERO;
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.05"));
        BigDecimal total = subtotal.add(shipping).add(tax);

        return CartDetailDto.builder()
                .items(items)
                .subtotal(subtotal)
                .shipping(shipping)
                .tax(tax)
                .total(total)
                .build();
    }

    private int getCartTotalItems(Integer cartId) {
        return cartItemRepository.findByCartId(cartId).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
