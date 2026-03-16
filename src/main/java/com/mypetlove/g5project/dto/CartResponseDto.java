package com.mypetlove.g5project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {
    private String status;
    private String message;
    private Integer totalItems;
}
