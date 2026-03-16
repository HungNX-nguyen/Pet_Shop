package com.mypetlove.g5project.dto.DtoRequest;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDto {
    private List<Integer> productIds;
    private String paymentMethod;
    private String shippingAddress;
}
