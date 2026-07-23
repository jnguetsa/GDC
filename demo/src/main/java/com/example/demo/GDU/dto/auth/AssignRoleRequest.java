package com.example.demo.GDU.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class AssignRoleRequest {
    @NotNull(message = "User ID is required")
    private Long utilisateurId;

    @NotEmpty(message = "At least one role must be provided")
    private Set<Long> roleIds;
}
