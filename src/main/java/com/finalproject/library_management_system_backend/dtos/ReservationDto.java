package com.finalproject.library_management_system_backend.dtos;

import com.finalproject.library_management_system_backend.model.ReservationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDto {

    private Long id;
    private ReservationStatus status;
    private LocalDateTime reservationDate;
    private LocalDateTime returnDate;

    private Long userId;
    private String userFirstName;
    private Long bookId;
    private String bookTitle;
}
