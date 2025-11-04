package com.finalproject.library_management_system_backend.repositories;

import com.finalproject.library_management_system_backend.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    Optional<UserType> findByTypeName(String typeName);
}
