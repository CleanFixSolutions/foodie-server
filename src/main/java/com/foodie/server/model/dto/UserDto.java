package com.foodie.server.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

//    @NotBlank(message = "Email required")
//    private String email;

    @NotBlank(message = "Username required")
    @JsonAlias(value = {"name", "nickname", "user", "login"})
    private String username;

    @NotBlank(message = "Password required")
    private String password;

}
