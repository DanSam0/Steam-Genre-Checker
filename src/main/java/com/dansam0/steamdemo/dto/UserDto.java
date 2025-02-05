package com.dansam0.steamdemo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Pattern(regexp = "^(?=\\S+$).+$", message = "contains white space")
    private String name;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$",
            message = "must have 8 - 20 characters, " +
            "include a capital letter, " +
            "use at least one lowercase letter and " +
            "consists of at least one digit")
    private String password;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String confirmPassword;

}
