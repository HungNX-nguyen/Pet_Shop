package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
    Optional<CartItem> findByCartCustomerUsernameAndProductId(String username, Integer productId);
    List<CartItem> findByCartId(Integer cartId);
}
