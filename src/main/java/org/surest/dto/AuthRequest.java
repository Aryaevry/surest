package org.surest.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
