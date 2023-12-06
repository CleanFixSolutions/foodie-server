package com.foodie.server.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {

    @JsonIgnore
    private String oldUsername = "";

    @Pattern(regexp = "[a-zA-Z0-9_-]{1,25}",
            message = "username must contain only letters, '_' or '-'. Max length=25")
    @JsonProperty("username")
    @JsonAlias(value = {"name", "nickname", "user", "login"})
    @Schema(name = "username", example = "mishok")
    private String newUsername;

    @Size(min = 5, max = 25)
    @JsonProperty("password")
    @JsonAlias(value = {"new_password"})
    @Schema(name = "password", example = "12345")
    private String newPassword;

    @JsonIgnore
    public boolean isUpdated() {
        return newPassword != null
               || (newUsername != null && !getOldUsername().equals(getNewUsername()));
    }
}
