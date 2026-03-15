package com.mypetlove.g5project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Vũ khí tối thượng: Tự động sinh toàn bộ Getter, Setter, toString...
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String fullName;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;

}