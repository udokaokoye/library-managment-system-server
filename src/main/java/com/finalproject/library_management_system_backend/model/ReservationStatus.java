package com.finalproject.library_management_system_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


public enum ReservationStatus {
    RESERVED,
    BORROWED,
    LATE,
    RETURNED,
    CANCELED,
    OVERDUE,
    LATE_RETURNED
}