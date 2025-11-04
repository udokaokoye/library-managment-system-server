package com.finalproject.library_management_system_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String firstName;
    private String lastName;
}
