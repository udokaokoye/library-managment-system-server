package com.finalproject.library_management_system_backend.services;


import com.finalproject.library_management_system_backend.dtos.RegisterUserRequest;
import com.finalproject.library_management_system_backend.dtos.UserDto;
import com.finalproject.library_management_system_backend.mappers.UserMapper;
import com.finalproject.library_management_system_backend.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EntityManager entityManager;

    public UserDto getUserDetailsByEmail(String email){
        var userEntity = userRepository.findByEmail(email).orElseThrow();
        return userMapper.toDto(userEntity);
    }

    @Transactional
    public UserDto createUser(@RequestBody RegisterUserRequest request){
        var userEntity = userMapper.toEntity(request);
        entityManager.persist(userEntity);
        return userMapper.toDto(userEntity);
 }


}
