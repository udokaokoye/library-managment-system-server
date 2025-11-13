package com.finalproject.library_management_system_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_type")
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @OneToMany(
        mappedBy = "userType",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    @ToString.Exclude
    private List<User> users = new ArrayList<>();
}
