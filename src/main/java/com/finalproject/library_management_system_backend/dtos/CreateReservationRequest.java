package com.finalproject.library_management_system_backend.dtos;

import lombok.Data;

@Data
public class CreateReservationRequest {
    private Long userId;
    private Long bookId;
    private Integer DaysToKeep;
}
