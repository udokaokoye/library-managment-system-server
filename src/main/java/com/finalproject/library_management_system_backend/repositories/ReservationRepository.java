package com.finalproject.library_management_system_backend.repositories;

import com.finalproject.library_management_system_backend.model.Reservation;
import com.finalproject.library_management_system_backend.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserEmail(String email);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByStatusAndExpectedReturnDateBefore(ReservationStatus status, LocalDateTime now);

    boolean existsByBookId(Long bookId);
}

