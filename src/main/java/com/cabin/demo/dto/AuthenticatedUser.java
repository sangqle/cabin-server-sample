package com.cabin.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticatedUser {
    private Long userId;
    private String email;

    public AuthenticatedUser(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}
