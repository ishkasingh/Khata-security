package com.example.KhataWebSecurity.DTO;

import lombok.*;

@Data
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}
