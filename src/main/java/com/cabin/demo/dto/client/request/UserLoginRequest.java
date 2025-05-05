package com.cabin.demo.dto.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    String email;
    String password;
}
