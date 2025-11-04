package com.finalproject.library_management_system_backend.mappers;

import com.finalproject.library_management_system_backend.dtos.RegisterUserRequest;
import com.finalproject.library_management_system_backend.dtos.UserDto;
import com.finalproject.library_management_system_backend.model.User;
import com.finalproject.library_management_system_backend.model.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target= "userTypeId", source= "userTypeId")
    User toEntity(RegisterUserRequest request);

    default UserType map(Long userTypeId) {
        if (userTypeId == null) {
            return null;
        } else {
            UserType userType = new UserType();
            userType.setId(userTypeId);
            return userType;
        }
    }
}
