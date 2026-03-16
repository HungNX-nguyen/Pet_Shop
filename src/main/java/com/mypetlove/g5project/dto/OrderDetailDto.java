package com.mypetlove.g5project.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDto {

    private Integer orderId;
    private String orderCode;
    private String status;
    private String shippingAddress;
    private String paymentMethod;
    private Boolean paymentStatus;

    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal tax;
    private BigDecimal total;

    private List<OrderItemDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Integer productId;
        private String productName;
        private String imageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subTotal;
    }
}