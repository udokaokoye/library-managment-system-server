package com.finalproject.library_management_system_backend.dtos;

import com.finalproject.library_management_system_backend.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long userTypeId;
}
