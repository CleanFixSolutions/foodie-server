package com.foodie.server.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "Username required")
    @Pattern(regexp = "[a-zA-Z0-9_-]{1,25}",
            message = "username must contain only letters, '_' or '-'. Max length=25")
    @JsonAlias(value = {"username", "name", "nickname", "user", "login"})
    @Schema(name = "username", example = "mishok")
    private String username;

    @NotBlank(message = "Password required")
    @Size(min = 5, max = 25)
    @Schema(name = "password", example = "12345")
    private String password;

}
